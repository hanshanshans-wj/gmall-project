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

    @Override
    public List<UserInfo> findUserInfo(UserInfo userInfo) {
        List<UserInfo> select = userInfoMapper.select(userInfo);
        return select;

    }

    @Override
    public List<UserInfo> getUserByName(UserInfo userInfo) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("name",userInfo.getName());
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        return userInfos;
    }

    @Override
    public List<UserInfo> getUserByNickName(String nickName) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andLike("nickName",nickName);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        return userInfos;
    }

    @Override
    public void addUser(UserInfo userInfo) {
        userInfoMapper.insertSelective(userInfo);
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public void updateUserInfoExample(UserInfo userInfo) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("email",userInfo.getEmail());
        userInfoMapper.updateByExampleSelective(userInfo,example);
    }

    @Override
    public void delete(UserInfo userInfo) {
        userInfoMapper.delete(userInfo);
    }

    @Override
    public void deleteByExample() {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andBetween("phoneNum",4000,6000);

        userInfoMapper.deleteByExample(example);
    }

    @Override
    public void deleteByPrimaryKey(String id) {
        userInfoMapper.deleteByPrimaryKey(id);
    }
}
