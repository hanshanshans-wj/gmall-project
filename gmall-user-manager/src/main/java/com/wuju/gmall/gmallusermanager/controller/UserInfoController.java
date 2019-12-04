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

    @RequestMapping("findUserInfo")
    public List<UserInfo> findUserInfo(UserInfo userInfo){
        List<UserInfo> userInfos=userInfoService.findUserInfo(userInfo);
        return userInfos;
    }

    @RequestMapping("getUserByName")
    public List<UserInfo>  getUserByName(UserInfo userInfo){
        List<UserInfo> userInfos=userInfoService.getUserByName(userInfo);
        return userInfos;
    }

    @RequestMapping("getUserByNickName")
    public List<UserInfo> getUserByNickName(String nickName){
        List<UserInfo> userInfos=userInfoService.getUserByNickName(nickName);
        return userInfos;
    }
    //增加
    // localhost:8080/addUser?name=xxx&nickName=xxx
    @RequestMapping("addUser")
    public void addUser(UserInfo userInfo){
        userInfoService.addUser(userInfo);

    }
    //根据主键修改
    @RequestMapping("updateUserInfo")
    public void updateUserInfo(UserInfo userInfo){
        userInfoService.updateUserInfo(userInfo);
    }

    @RequestMapping("updateUserInfoExample")
    public void updateUserInfoExample(UserInfo userInfo){
        userInfoService.updateUserInfoExample(userInfo);
    }
    @RequestMapping("delete")
    public void delete(UserInfo userInfo){
        userInfoService.delete(userInfo);
    }
    @RequestMapping("deleteByExample")
    public void deleteByExample(){
        userInfoService.deleteByExample();
    }
    @RequestMapping("deleteByPrimaryKey")
    public void deleteByPrimaryKey(String id){
        userInfoService.deleteByPrimaryKey(id);
    }
}
