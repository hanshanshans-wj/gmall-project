package com.wuju.gmall.gmallorderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wuju.gmall.Service.UserInfoService;
import com.wuju.gmall.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    @Reference
    private UserInfoService userInfoService;
    @RequestMapping("getUserAddressByUserId")
    public List<UserAddress> getUserAddressByUserId(String userId){
        List<UserAddress> userAddressByUserId = userInfoService.getUserAddressByUserId(userId);
        return userAddressByUserId;
    }
}
