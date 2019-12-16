package com.wuju.gmall.gmallpassportweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wuju.gmall.Service.UserInfoService;
import com.wuju.gmall.UserInfo;
import com.wuju.gmall.gmallpassportweb.config.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassWordController {
    @Value("${token.key}")
    private String signKey;
    @Reference
    private UserInfoService userInfoService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UserInfo userInfo){
        String header = request.getHeader("X-forwarded-for");
        if (userInfo!=null){
            UserInfo loginInfo=userInfoService.login(userInfo);
            if (loginInfo==null){
                return "fail";
            }else {
                //生成token
                Map<String,Object> map=new HashMap<>();
                map.put("userId",loginInfo.getId());
                map.put("nickName",loginInfo.getNickName());
                String token = JwtUtil.encode(signKey, map, header);
                return token;
            }
        }
        return "fail";
    }
    //验证方法
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        //获取token和盐
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
//        a.	从url 路径上得到token ，salt
//        b.	使用jwt 解密得到用户的数据{map}
//        c.	获取map 中的userId 查询缓存
//        d.	true:success 	false:fail
//        e.	测试：

        Map<String, Object> decode = JwtUtil.decode(token, signKey, salt);
        if (decode!=null){
            String userId = (String) decode.get("userId");
            UserInfo userInfo=userInfoService.verify(userId);
            if (userInfo!=null){
                return "success";
            }
        }
    return "fail";
    }
}
