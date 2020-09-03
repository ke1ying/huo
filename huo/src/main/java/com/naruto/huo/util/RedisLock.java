package com.naruto.huo.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RedisLock {
    private final static Log log = LogFactory.getLog(RedisLock.class);
    @Autowired
    private RedisTemplate redisTemplate;

    public static final String UNLOCK_LUA;

    //过期时间3秒
    public static final long expire=3000L;

    //延迟重试时间3秒
    public static final long sleepTime=3000L;

    //重试次数
    public static final int retry=10;

    private static AtomicBoolean isOpen = new AtomicBoolean(true);

    public static Thread thread;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }


    /**onlineList
     *  加锁
     * */
    public boolean lock(String key,String requestId) {
        try {

            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                //使用uuid和 当前线程的名字作为 存储值
                //NX 当key不存在时，我们进行set操作；若key已经存在，则不做任何操作
                //PX 给这个key加一个过期的设置，具体时间由第五个参数决定
                return commands.set(key, requestId, "NX", "PX", expire);
            };
            String result = (String) redisTemplate.execute(callback);
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            log.error("set redis occured an exception:",e);
        }
        return false;
    }

    /**
     *
     * Title: lock
     * Description:
     * 1、加锁的时候开启一个守护线程，防止任务执行时间大于锁过期时间而导致并发问题，
     * 	   由守护线程定时延长锁过期时间，保证当任务还在执行的时候，锁就不会过期
     * 2、如果不设置成守护线程，那么如果主线程只执行过程中遇到异常中断了，子线程仍然在运行，就会无休止地就延长锁过期时间，就会有问题
     *    守护线程就可以保证主线程断了，子线程也会中断，就不会再延长锁过期时间了
     *
     * @param
     * @param
     * @return
     * @author lifeilong
     */
//    public boolean lock(String key,String requestId) {
//        try {
//
//            RedisCallback<String> callback = (connection) -> {
//                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                //使用uuid和 当前线程的名字作为 存储值
//                //NX 当key不存在时，我们进行set操作；若key已经存在，则不做任何操作
//                //PX 给这个key加一个过期的设置，具体时间由第五个参数决定
//                return commands.set(key, requestId, "NX", "PX", expire);
//            };
//            String result = (String) redisTemplate.execute(callback);
//
//            //拿到了锁
//            if(!StringUtils.isEmpty(result)){
//            	//isHappened保证一个主线程只能开一个子线程
//            	if(isOpen.get()){
//            		//开启守护线程
//            		Thread thread = new Thread(new DaemonThread(redisTemplate, key));
//            		thread.setDaemon(true);
//                    thread.start();
//                    isOpen.set(false);
//                }
//            	return true;
//            }
//            return false;
//        } catch (Exception e) {
//            log.error("set redis occured an exception:",e);
//        }
//        return false;
//    }

    //子线程，定时延长锁过期时间
    static class DaemonThread implements Runnable{
        private RedisTemplate redisTemplate;
        private String key;
        public DaemonThread(RedisTemplate redisTemplate, String key){
            this.redisTemplate = redisTemplate;
        }
        @Override
        public void run() {
            while (true){
                Long ttl = redisTemplate.getExpire(key);
                if(ttl != null && ttl > 0){
                    redisTemplate.expire(key, ttl + (expire - 10000), TimeUnit.MILLISECONDS);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("分布式锁续期失败！", e);
                }
            }
        }
    }

    /**
     *  生成value
     * */
    public String getRequestId(){
        String uuid = UUID.randomUUID().toString()+Thread.currentThread().getName();
        return uuid.replace("-","");
    }



    /**
     *  获取uuid
     * */
    public String getValue(String key) {
        String result ="";
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                return commands.get(key);
            };
            result = (String) redisTemplate.execute(callback);
            return result;
        } catch (Exception e) {
            log.error("get redis occured an exception:" ,e);
        }
        return result;
    }

    /**
     * 解锁
     * @param key 锁标识
     * @param requestId uuid
     * */
    public boolean unLock(String key,String requestId) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(requestId);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }
                return 0L;
            };
            Long result = (Long) redisTemplate.execute(callback);

            return result != null && result > 0;
        } catch (Exception e) {
            log.error("release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }

    /**
     * 重试机制
     * @param key 锁标识
     * @return
     */
    public Boolean lockRetry(String key,String uuid){
        Boolean flag = false;
        try {
            for (int i=0;i<retry;i++){
                log.info("第"+i+"尝试添加|key="+key+"|uuid="+uuid);
                flag = lock(key,uuid);
                if(flag){
                    log.info("尝试添加分布式锁成功|key="+key+"|uuid="+uuid);
                    break;
                }else {
                    Thread.sleep(sleepTime);
                }

            }
        }catch (Exception e){
            log.error("重试过程中添加锁出现异常",e);
            //e.printStackTrace();
        }
        return flag;
    }

}
