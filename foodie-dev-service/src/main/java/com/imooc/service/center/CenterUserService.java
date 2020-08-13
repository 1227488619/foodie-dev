package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service.center
 * @date 2020/7/26 17:14
 */
public interface CenterUserService {

    // 查询用户信息
    public Users queryUserInfo (String userId);

    // 修改用户信息
    public Users updateUserInfo (String userId, CenterUserBO centerUserBO);

    // 更新用户头像
    public Users updateUserFace(String userId, String faceUrl);
}
