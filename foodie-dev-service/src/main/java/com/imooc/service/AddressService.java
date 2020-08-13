package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/24 22:17
 */
public interface AddressService {
    // 查询user所有的地址
    public List<UserAddress> queryAll(String userId);

    // 增加地址
    public void add(AddressBO addressBO);

    // 删除地址
    public void deleteByUserId(String userId, String addressId);

    // 更新地址
    public void updateById(AddressBO addressBO);

    // 设置默认地址
    public void setDefault(String userId, String addressId);

    // 根据userId  addressId查询地址信息
    public UserAddress getAddress(String userId, String addressId);

}
