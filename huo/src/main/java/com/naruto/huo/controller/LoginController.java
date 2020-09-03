package com.naruto.huo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.naruto.huo.util.RedisLock;
import com.naruto.huo.util.RedisUtil;
import org.apache.axis.client.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.naruto.huo.model.KyUser;
import com.naruto.huo.service.LoginService;

@Controller
public class LoginController {

    private Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisLock redisLock;

    @RequestMapping("/")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        return mv;
    }

    /*
     * 登入
     * */
    @RequestMapping(value = "/indexFir")
    @ResponseBody
    public Map index(HttpServletRequest request, @RequestParam("name") String name
            , @RequestParam("password") String password, HttpSession session) {
        Service service = new Service();
        KyUser u = new KyUser();
        u.setName(name);
        u.setPassword(password);
        KyUser cunzai = loginService.queryUser(u);
        Map<String, Object> map = new HashMap<>();
        if (cunzai == null) {
            map.put("result", "用户名密码不正确，请重新输入!");
            map.put("flag", 0);
            return map;
        }
        session.setAttribute("userName", name);
        map.put("result", "登入成功!");
        map.put("flag", 1);
        return map;
    }

    @RequestMapping(value = "/indenGo")
    public String indenGo() {
        return "/index";
    }

    @RequestMapping(value = "/register")
    public String register() {
        return "register";
    }

    /*
     * 注册
     * */
    @RequestMapping(value = "/zhuceInsert")
    @ResponseBody
    public Map zhuceInsert(@RequestParam("name") String name
            , @RequestParam("password") String password) {
        Map<String, Object> map = new HashMap<>();
        KyUser u = new KyUser();
        u.setName(name);

        boolean b = redisLock.lock(name, "_name");
        log.info("当前为：" + b);
        if(b){
            u.setPassword(password);
            u.setStatus("1");
            Integer count = loginService.insertKyUser(u);
            if(count == 0){
                map.put("flag",0);
                map.put("result","注册失败");
            }
            map.put("flag", 1);
            map.put("result", "注册成功,请稍等!");
        }else{
            map.put("flag",2);
            map.put("result","已注册，请稍等");
        }
        return map;
    }

}
