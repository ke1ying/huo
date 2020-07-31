package com.naruto.huo.controller.wx;

import com.naruto.huo.common.WeixinUtil;
import com.naruto.huo.model.AccessToken;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WxController {

    @Autowired
    private MessageDispatcher messageDispatcher;

    @GetMapping("/wx")
    public void login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
                out.write(echostr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    @PostMapping(value = "/wx",produces = "application/xml;charset=utf-8")
    public String handler(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        /*创建菜单*/
        String accessToken = WeixinUtil.getAccess_Token(session,request);
        String menu = MenuUtil.initMenu();
        System.out.println("menu:"+menu);
        System.out.println("创建菜单的最终accessToken:"+accessToken);
        int result = MenuUtil.createMenu(accessToken,menu);
        if(result==0){
            System.out.println("菜单创建成功");
        }else{
            System.out.println("错误码"+result);
        }

     /*   //将微信请求xml转为map格式，获取所需的参数
        Map<String,String> mapJieshou = MessageUtil.xmlToMap(request);
        String Content = mapJieshou.get("Content");*/

        Map<String, String> map = WxController.parseXml(request);
        String msgType = map.get("MsgType");
        if (MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)) {
            return messageDispatcher.processEvent(map);
        }else{
            return messageDispatcher.processMessage(map,session,request);
        }
    }
/*    @PostMapping("/wx")
    public void handler(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, String> parseXml = WxController.parseXml(request);
        String msgType = parseXml.get("MsgType");
        String content = parseXml.get("Content");
        String fromusername = parseXml.get("FromUserName");
        String tousername = parseXml.get("ToUserName");
        System.out.println(msgType);
        System.out.println(content);
        System.out.println(fromusername);
        System.out.println(tousername);
    }*/
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elementList = root.elements();
        for (Element e : elementList)
            map.put(e.getName(), e.getText());
        inputStream.close();
        return map;
    }
}
