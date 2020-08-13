package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service.impl
 * @date 2020/7/24 22:17
 */
@Service
public class AdderssServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private Sid sid;

    @Override
    public List<UserAddress> queryAll(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void add(AddressBO addressBO) {
        //  判断当前用户是否存在地址，如果没有，则新增为‘默认地址’  0:普通地址  1：默认地址

        Integer isDefault = YesOrNo.NO.type;
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (addressList == null || addressList.isEmpty() || addressList.size() == 0) {
            isDefault = YesOrNo.YES.type;
        }

        String addressId = sid.nextShort();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteByUserId(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);

        userAddressMapper.delete(userAddress);

        UserAddress selectUserAddress = new UserAddress();
        selectUserAddress.setUserId(userId);

        List<UserAddress> addressList = userAddressMapper.select(selectUserAddress);
        if (addressList != null && !addressList.isEmpty() && addressList.size() != 0) {
            for (UserAddress ua : addressList) {
                if (YesOrNo.YES.type.equals(ua.getIsDefault())) {
                    return;
                }
            }
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress = addressList.get(0);
            updateUserAddress.setIsDefault(YesOrNo.YES.type);
            updateUserAddress.setUpdatedTime(new Date());
            userAddressMapper.updateByPrimaryKeySelective(updateUserAddress);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateById(AddressBO addressBO) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setUpdatedTime(new Date());
        userAddress.setId(addressBO.getAddressId());

        // Selective 是当在更新时，为空的值不会覆盖相关的字段
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void setDefault(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        List<UserAddress> userAddressList = userAddressMapper.select(userAddress);

        for (UserAddress ua : userAddressList) {
            if (YesOrNo.YES.type.equals(ua.getIsDefault())) {
                ua.setIsDefault(YesOrNo.NO.type);
                ua.setUpdatedTime(new Date());
                userAddressMapper.updateByPrimaryKeySelective(ua);
            }
            if (ua.getId().equals(addressId)) {
                ua.setIsDefault(YesOrNo.YES.type);
                ua.setUpdatedTime(new Date());
                userAddressMapper.updateByPrimaryKeySelective(ua);
            }
        }

//        UserAddress updateUserAddress = new UserAddress();
//        updateUserAddress.setUserId(userId);
//        updateUserAddress.setId(addressId);
//        updateUserAddress.setIsDefault(YesOrNo.YES.type);
//        updateUserAddress.setUpdatedTime(new Date());
//        userAddressMapper.updateByPrimaryKeySelective(updateUserAddress);

    }

    @Override
    public UserAddress getAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);

        return userAddressMapper.selectOne(userAddress);
    }
}
