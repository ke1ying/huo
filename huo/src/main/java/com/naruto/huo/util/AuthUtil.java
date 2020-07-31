package com.naruto.huo.util;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AuthUtil {

    public static final String APPID = "wxf06a9f13d673819b";

    public static final String APPSECRET = "27226eab5a8bbfca144cfb6d9e2f12d9";


    public static JSONObject doGetJson(String url) throws ClientProtocolException, IOException {

        JSONObject jsonObject = null;

        //首先初始化HttpClient对象

        DefaultHttpClient client = new DefaultHttpClient();

        //通过get方式进行提交

        HttpGet httpGet = new HttpGet(url);

        //通过HTTPclient的execute方法进行发送请求

        HttpResponse response = client.execute(httpGet);

        //从response里面拿自己想要的结果

        HttpEntity entity = response.getEntity();

        if (entity != null) {

            String result = EntityUtils.toString(entity, "UTF-8");

            jsonObject = jsonObject.fromObject(result);

        }

        //把链接释放掉

        httpGet.releaseConnection();

        return jsonObject;

    }

    public static JSONObject getRefreshToken(String refresh_token) throws IOException {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + APPID + "&grant_type=refresh_token&refresh_token=" + refresh_token;

        JSONObject jsonObject = null;

        //首先初始化HttpClient对象

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        //通过HTTPclient的execute方法进行发送请求

        HttpResponse response = client.execute(httpGet);

        //从response里面拿自己想要的结果

        HttpEntity entity = response.getEntity();

        if (entity != null) {

            String result = EntityUtils.toString(entity, "UTF-8");

            jsonObject = jsonObject.fromObject(result);

        }

        //把链接释放掉

        httpGet.releaseConnection();

        return jsonObject;
    }
}
