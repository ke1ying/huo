package com.naruto.huo.controller;
import com.naruto.huo.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
public class WxLoginController{

    private Logger log = LoggerFactory.getLogger(WxLoginController.class);

    /**
     *
     */

    private static final long serialVersionUID = 1L;

    @RequestMapping("/wxLogin")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("进入WxLoginController");
        //第一步：引导用户进入授权页面同意授权，获取code
        //回调地址
        String backUrl = "http://keying.online/callBack";         //第1种情况使用

       // String backUrl = "http://suliu.free.ngrok.cc/WxAuth/wxCallBack";//第2种情况使用，这里是web.xml中的路径


        //授权页面地址

        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + AuthUtil.APPID

                + "&redirect_uri=" + URLEncoder.encode(backUrl)

                + "&response_type=code"

                + "&scope=snsapi_userinfo"

                + "&state=STATE#wechat_redirect";


        //重定向到授权页面

        response.sendRedirect(url);

    }

}
