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

    /**
     * 根据条件查询
     * @param userInfo
     * @return
     */
    List<UserInfo> findUserInfo(UserInfo userInfo);

    /**
     * 根据条件严格查询
     * @param userInfo
     * @return
     */
    List<UserInfo> getUserByName(UserInfo userInfo);

    /**
     * 模糊查询
     * @param nickName
     * @return
     */
    List<UserInfo> getUserByNickName(String nickName);

    /**
     * 增加
     * @param userInfo
     */
    void addUser(UserInfo userInfo);

    /**
     * 根据主键修改
     * @param userInfo
     */
    void updateUserInfo(UserInfo userInfo);

    /**
     * 根据其他条件修改
     * @param userInfo
     */
    void updateUserInfoExample(UserInfo userInfo);

    /**
     * 根据条件删除
     * @param userInfo
     */
    void delete(UserInfo userInfo);

    /**
     * 根据属性，以及范围删除！
     * @param
     */
    void deleteByExample();

    /**
     * 根据主键删除数据！
     * @param id
     */
    void deleteByPrimaryKey(String id);
}
