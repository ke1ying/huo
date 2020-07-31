package com.naruto.huo.controller.wx;

import com.naruto.huo.model.TextMessage;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;

@Component
public class MessageDispatcher {
    public static String processMessage(Map<String, String> map, HttpSession session, HttpServletRequest request) throws FileNotFoundException {
        String openid = map.get("FromUserName"); //用户 openid
        String mpid = map.get("ToUserName");   //公众号原始 ID
        String content = map.get("Content");
        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
            //普通文本消息
            TextMessage txtmsg = new TextMessage();
            txtmsg.setToUserName(openid);
            txtmsg.setFromUserName(mpid);
            txtmsg.setCreateTime(new Date().getTime());
            txtmsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            if("宣京晶".equals(content)){
                txtmsg.setContent("宣京晶是老婆!没错~");
                //发送图片
            }else if("图片".equals(content)){
                MessageUtil util = new MessageUtil();
                return util.initMessage(openid, mpid,session,request);
            }else{
                txtmsg.setContent("您输入的是:"+content);
            }
            return MessageUtil.textMessageToXml(txtmsg);
        }
        return null;
    }
    public String processEvent(Map<String, String> map) {
        //在这里处理事件
        return "123";
    }
}
