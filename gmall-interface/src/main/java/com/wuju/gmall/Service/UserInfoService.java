package com.wuju.gmall.Service;

import com.wuju.gmall.UserAddress;
import com.wuju.gmall.UserInfo;

import java.util.List;

public interface UserInfoService {
    /**
     * 返回所有数据
     * @return
     */
    List<UserInfo> findAll();

    /**
     * 根据用户id查询地址
     * @param userId
     * @return
     */
    public List<UserAddress> getUserAddressByUserId(String userId);
}
