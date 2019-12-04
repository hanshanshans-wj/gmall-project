package com.wuju.gmall.gmallusermanager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wuju.gmall.UserAddress;
import com.wuju.gmall.UserInfo;
import com.wuju.gmall.Service.UserInfoService;
import com.wuju.gmall.gmallusermanager.mapper.UserAddressMapper;
import com.wuju.gmall.gmallusermanager.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Override
    public List<UserInfo> findAll() {
        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }
    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {
        Example example = new Example(UserAddress.class);
        example.createCriteria().andEqualTo("userId",userId);
        List<UserAddress> userAddresses = userAddressMapper.selectByExample(example);
        return userAddresses;
    }
}
