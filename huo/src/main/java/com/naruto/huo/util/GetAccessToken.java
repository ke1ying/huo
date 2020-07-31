package com.naruto.huo.util;

import com.naruto.huo.controller.WxIndexController;
import com.naruto.huo.model.AccessToken;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAccessToken implements Runnable {
// 測試號
 //自己的id和密码此次是测试号

    public static String AppId = "wxf06a9f13d673819b";
    public static String secret = "27226eab5a8bbfca144cfb6d9e2f12d9";

/*

    // 第一次直給实例
    public static AccessToken accessToken = new GetAccessToken().getAccessToken(AppId, secret);//下面有代码
    // 默認等一次实例，线程自动赋值
    public static AccessToken accessToken2 = accessToken;
    // 启动表示类防止重复启动
*/

    @Override
    public void run() {

      /*  while (true) {
            try {
                if (null != accessToken2) {
                // 当前 休眠7000秒，accessToken 从赋值
                    Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
                // Thread.sleep(3000);
                    accessToken2 = new GetAccessToken().getAccessToken(AppId, secret);
                // System.out.println(accessToken2.getAccess_token());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }




    /**
     * 获取accessToken
     * 
     * @return
     * @throws
     */
    public AccessToken getAccessToken(String AppId, String secret) {
        System.out.println("进入线程定时获取获取accessToken");
        AccessToken at = new AccessToken();
        try {
            String grant_type = "client_credential";// refresh_token
            String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + AppId
                    +"&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
      /*      String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grant_type + "&appid=" + AppId
                    + "&secret=" + secret;*/
            URL url_1 = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) url_1.openConnection();
            huc.setRequestMethod("GET");
            huc.setDoOutput(true);
            huc.setDoInput(true);
            huc.connect();


            InputStream inputStream = huc.getInputStream();
            int size = inputStream.available();
            byte[] bs = new byte[size];
            inputStream.read(bs);
            String message = new String(bs, "UTF-8");


            // 获取access_token
            JSONObject jsonObject = JSONObject.fromObject(message);
            System.out.println("进入线程定时获取jsonObject为："+jsonObject);
            String accessToken = jsonObject.getString("access_token");
            int expires_in = Integer.parseInt(jsonObject.getString("expires_in"));
            String openId = jsonObject.getString("openid");

            at.setToken(accessToken);
            at.setExpiresIn(expires_in);
            System.out.println("进入线程定时获取accessToken为："+accessToken);
            System.out.println("进入线程定时获取openId为："+openId);
        } catch (Exception e) {


        }
        return at;
    }
}
