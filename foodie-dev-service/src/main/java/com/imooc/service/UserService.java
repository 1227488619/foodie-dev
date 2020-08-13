package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/14 16:54
 */
public interface UserService {
    // 判断用户名是否存在
    public boolean queryUsernameIsExist(String username);

    // 创建用户
    public Users createUser(UserBO userBO);

    //用户登录
    public Users queryUserForLogin(String username, String password);
}
