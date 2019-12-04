package com.wuju.gmall.gmallusermanager.controller;

import com.wuju.gmall.UserInfo;
import com.wuju.gmall.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @RequestMapping("findAll")
    public List<UserInfo> findAll(){
        List<UserInfo> all = userInfoService.findAll();
        return all;
    }
}
