package com.naruto.huo.controller;

import com.naruto.huo.model.UserVx;
import com.naruto.huo.util.AuthUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

@Controller
public class WxIndexController {

    /**
     *
     */
    private Logger log = LoggerFactory.getLogger(WxIndexController.class);

    private static final long serialVersionUID = 1L;

    //1. 获取JDBCTemplate对象
    @Autowired
    private JdbcTemplate jdbcTemplate;


    private String dbUrl;

    private String driverClassName;

    private String userName;

    private String passWord;


    private Connection conn = null;

    private PreparedStatement ps = null;

    private ResultSet rs = null;


    //初始化数据库
    public void init(ServletConfig config) throws ServletException {


        //加载驱动

        try {

            this.dbUrl = config.getInitParameter("dbUrl");

            this.driverClassName = config.getInitParameter("driverClassName");

            this.userName = config.getInitParameter("userName");

            this.passWord = config.getInitParameter("passWord");

            Class.forName(driverClassName);

        } catch (ClassNotFoundException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

    }

    @RequestMapping("/callBack")
    protected ModelAndView callBack(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("进入获取code后台");
        //第二步：通过code换取网页授权access_token


        //从request里面获取code参数(当微信服务器访问回调地址的时候，会把code参数传递过来)

        String code = request.getParameter("code");


        System.out.println("code:" + code);


        //获取code后，请求以下链接获取access_token

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AuthUtil.APPID

                + "&secret=" + AuthUtil.APPSECRET

                + "&code=" + code

                + "&grant_type=authorization_code";


        //通过网络请求方法来请求上面这个接口

        JSONObject jsonObject = AuthUtil.doGetJson(url);


        System.out.println("==========================jsonObject" + jsonObject);


        //从返回的JSON数据中取出access_token和openid，拉取用户信息时用

        String token = jsonObject.getString("access_token");
        System.out.println("第一次的token:"+token);
        String openid = jsonObject.getString("openid");
        String refresh_token = jsonObject.getString("refresh_token");

        // 第三步：刷新access_token（如果需要）
        JSONObject jsonObjectRefresh = AuthUtil.getRefreshToken(refresh_token);
        // 第四步：拉取用户信息(需scope为 snsapi_userinfo)
        System.out.println("==========================jsonObjectRefresh" + jsonObjectRefresh);
        String tokenR = jsonObject.getString("access_token");
        String openidR = jsonObject.getString("openid");
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + tokenR
                + "&openid=" + openidR
                + "&lang=zh_CN";

        //通过网络请求方法来请求上面这个接口

        JSONObject userInfo = AuthUtil.doGetJson(infoUrl);

        //存储此用户
        UserVx userVx = new UserVx();
        userVx.setOpenId((String)userInfo.get("openid"));
        userVx.setPassword("123456");
        userVx.setUpdateTime(new Date());
        userVx.setCreateTime(new Date());
        log.info("获取到openid:"+(String)userInfo.get("openid"));
        Integer count = jdbcTemplate.queryForObject("select count(1) from user_wx where openid = ?",Integer.class,new Object[]{(String)userInfo.get("openid")});
        log.info("此用户存在几个："+count);
        if(count == 0){
            String sql = "insert into user_wx (openid,password,create_time,update_time) values (?,?,?,?)";
            jdbcTemplate.update(sql,(String)userInfo.get("openid"),"123456",new Date(),new Date());
            log.info("插入一条数据成功");
        }else{
            String sql = "update user_wx set update_time = ? where openid = ?";
            jdbcTemplate.update(sql,new Date(),(String)userInfo.get("openid"));
        }

        //第1种情况：使用微信用户信息直接登录，无需注册和绑定

        request.setAttribute("info", userInfo);
        //直接跳转
//        request.getRequestDispatcher("/index1Wx.html").forward(request, response);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index1Wx");
        return  mv;
        //第2种情况： 将微信与当前系统的账号进行绑定(需将第1种情况和@WebServlet("/callBack")注释掉)

        //第一步，根据当前openid查询数据库，看是否该账号已经进行绑定

   /*     try {

            String nickname = getNickName(openid);

            if (!"".equals(nickname)) {

                //已绑定

                request.setAttribute("nickname", nickname);

                request.getRequestDispatcher("/indexWx2.html").forward(request, response);

            } else {

                //未绑定

                request.setAttribute("openid", openid);

                request.getRequestDispatcher("/loginWx.html").forward(request, response);

            }

        } catch (SQLException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }*/


    }


    //数据库的查询

    public String getNickName(String openid) throws SQLException {

        String nickName = "";

        //创建数据库链接

        conn = DriverManager.getConnection(dbUrl, userName, passWord);

        String sql = "select nickname from user where openid = ?";

        ps = conn.prepareStatement(sql);

        ps.setString(1, openid);

        rs = ps.executeQuery();

        while (rs.next()) {

            nickName = rs.getString("nickname");

        }


        //关闭链接

        rs.close();

        ps.close();

        conn.close();


        return nickName;

    }


    //数据库的修改(openid的綁定)

    public int updateUser(String account, String password, String openid) throws SQLException {


        //创建数据库链接

        conn = DriverManager.getConnection(dbUrl, userName, passWord);

        String sql = "update user set openid = ? where account = ? and password = ?";

        ps = conn.prepareStatement(sql);

        ps.setString(1, openid);

        ps.setString(2, account);

        ps.setString(3, password);

        int temp = ps.executeUpdate();


        //关闭链接

        rs.close();

        ps.close();

        conn.close();


        return temp;

    }
/*

    //post方法，用来接受登录请求
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String account = request.getParameter("account");

        String password = request.getParameter("password");

        String openid = request.getParameter("openid");


        try {

            int temp = updateUser(account, password, openid);


            if (temp > 0) {

                String nickname = getNickName(openid);

                request.setAttribute("nickname", nickname);

                request.getRequestDispatcher("/indexWx2.html").forward(request, response);

                System.out.println("账号绑定成功");

            } else {

                System.out.println("账号绑定失败");

            }


        } catch (SQLException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }


    }*/


}
