package com.naruto.huo.controller;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import java.util.Arrays;


public class DdController {

    private static  final String URL =
            "https://oapi.dingtalk.com/robot/send?access_token=f8c7b43ce50c29dfabf23cbbe5df9cb469524ac43f36558e9d45af7d909f88ce";
    public static void main(String[] args) {
        //机器人地址
        DingTalkClient client = new DefaultDingTalkClient(URL);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("业务消息:代码发送");
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();//设置一个At对象，用于@用户
        at.setAtMobiles(Arrays.asList(//需要@的人
                "18557538570",
                "18084600080"
        ));
        request.setAt(at);
        OapiRobotSendResponse response;
        try {
            response=client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
