package com.naruto.huo.controller.wx;

import com.naruto.huo.common.AccessTokenThread;
import com.naruto.huo.common.UploadUtil;
import com.naruto.huo.common.WeixinUtil;
import com.naruto.huo.model.AccessToken;
import com.naruto.huo.model.Image;
import com.naruto.huo.model.ImageMessage;
import com.naruto.huo.model.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.axis.utils.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessageUtil {
    private static Logger log = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * 返回消息类型：文本
     */
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    /**
     * 返回消息类型：音乐
     */
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
    /**
     * 返回消息类型：图文
     */
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";
    /**
     * 返回消息类型：图片
     */
    public static final String RESP_MESSAGE_TYPE_Image = "image";
    /**
     * 返回消息类型：语音
     */
    public static final String RESP_MESSAGE_TYPE_Voice = "voice";
    /**
     * 返回消息类型：视频
     */
    public static final String RESP_MESSAGE_TYPE_Video = "video";
    /**
     * 请求消息类型：文本
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    /**
     * 请求消息类型：图片
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    /**
     * 请求消息类型：链接
     */
    public static final String REQ_MESSAGE_TYPE_LINK = "link";
    /**
     * 请求消息类型：地理位置
     */
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
    /**
     * 请求消息类型：音频
     */
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
    /**
     * 请求消息类型：视频
     */
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
    /**
     * 请求消息类型：推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";
    /**
     * 事件类型：VIEW(自定义菜单 URl 视图)
     */
    public static final String EVENT_TYPE_VIEW = "VIEW";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    /**
     * 事件类型：LOCATION(上报地理位置事件)
     */
    public static final String EVENT_TYPE_SCAN = "SCAN";

    public static final String FromUserName = "FromUserName";
    public static final String ToUserName = "ToUserName";
    public static final String MsgType = "MsgType";
    public static final String Content = "Content";
    public static final String Event = "Event";

    public static String textMessageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                boolean cdata = true;
                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    /**
     * 将微信的请求中参数转成map
     * @param request
     * @return
     */
    public static Map<String,String> xmlToMap(HttpServletRequest request){
        Map<String,String> map = new HashMap<String,String>();
        SAXReader reader = new SAXReader();
        InputStream in = null;
        try {
            in = request.getInputStream();
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element element : list) {
                map.put(element.getName(), element.getText());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return map;
    }
    /*处理图片*/
    /**
     * 将信息转为xml格式
     */
    public String messageToxml(ImageMessage imageMessage) {
        XStream xtream = new XStream();
        xtream.alias("xml", imageMessage.getClass());
        xtream.alias("Image", new Image().getClass());
        System.out.println("xtream.toString():"+xtream.toString());
        return xtream.toXML(imageMessage);
    }
    /**
     * 封装信息
     */
    public String initMessage(String FromUserName, String ToUserName,HttpSession session, HttpServletRequest request) throws FileNotFoundException {
        Image image = new Image();
        image.setMediaId(getmediaId(session,request));
        log.info("MediaId:"+image.getMediaId());
        ImageMessage message = new ImageMessage();
        message.setFromUserName(ToUserName);
        message.setToUserName(FromUserName);
        message.setCreateTime(new Date().getTime());
        message.setImage(image);
        log.info("ToUserName:"+ToUserName);
        log.info("FromUserName:"+FromUserName);
        message.setMsgType("image");
        log.info("message:"+message.toString());
        return messageToxml(message);
    }

    @Value("${config.extPath}")
    private String extPath;

    /**
     * 获取Media
     * @return
     */
    public String getmediaId(HttpSession session, HttpServletRequest request) throws FileNotFoundException {
        //获取classes目录绝对路径
        //ResourceUtils.getURL("classpath:").getPath();
        //String path = request.getSession().getServletContext().getRealPath("/static/pic/feng.png");

        //String path = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static/images/feng.png";
        String path = request.getServletContext().getRealPath("/")  + "WEB-INF/classes/static/images/feng.png";
        log.info("path:"+path);
        String accessToken = WeixinUtil.getAccess_Token(session,request);
        String mediaId = null;
        try {
            mediaId = UploadUtil.upload(path, accessToken.toString(), "image");

        } catch (KeyManagementException | NoSuchAlgorithmException
                | NoSuchProviderException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mediaId;
    }

/*处理结束*/

}

