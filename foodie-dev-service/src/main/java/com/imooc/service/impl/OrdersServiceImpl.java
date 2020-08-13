package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.OrdersBO;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrdersService;
import com.imooc.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service.impl
 * @date 2020/7/25 9:43
 */
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressService addressService;
    
    @Autowired
    private ItemService itemService;

    @Autowired
    private Sid sid;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrders(List<ShopcartBO> shopcartBOList, OrdersBO ordersBO) {

        String userId = ordersBO.getUserId();
        String addressId = ordersBO.getAddressId();
        String itemSpecIds = ordersBO.getItemSpecIds();
        Integer payMethod = ordersBO.getPayMethod();
        String leftMsg = ordersBO.getLeftMsg();
        // 邮费
        Integer postAmount = 0;

        // 1. 新订单数据保存
        String orderId = sid.nextShort();

        UserAddress userAddress = addressService.getAddress(userId, addressId);

        Orders order = new Orders();
        order.setId(orderId);
        order.setUserId(userId);
        // 在创建订单信息的时候，存储的当时用户的地址信息，
        order.setReceiverAddress(userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getDetail());
        order.setReceiverMobile(userAddress.getMobile());
        order.setReceiverName(userAddress.getReceiver());

        order.setPostAmount(postAmount);
        order.setPayMethod(payMethod);
        order.setLeftMsg(leftMsg);
        order.setIsComment(YesOrNo.NO.type);
        order.setIsDelete(YesOrNo.NO.type);
        order.setCreatedTime(new Date());
        order.setUpdatedTime(new Date());

        // 2. 循环根据itemSpecIds 保存订单商品信息表
        String[] itemSpecIdsList = itemSpecIds.split(",");
        Integer totalAmount = 0 ;   // 商品原价累计
        Integer realPayAmount = 0;  // 实际支付的价格

        List<ShopcartBO> removeShopcartList = new ArrayList<>();

        for (String itemSpecId : itemSpecIdsList) {

            // 整合redis之后，重新获取购物车中的数量
            ShopcartBO shopcartBO = getBuyCounts(shopcartBOList, itemSpecId);
            removeShopcartList.add(shopcartBO);
            int buyCount = shopcartBO.getBuyCounts();

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemsSpec = itemService.queryItemBySpecId(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCount;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCount;

            // 2.2 根据规格id，获得商品信息和图片
            String itemId = itemsSpec.getItemId();
            Items items = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            // 2.3 循环保存子订单到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItems = new OrderItems();
            subOrderItems.setId(subOrderId);
            subOrderItems.setOrderId(orderId);
            subOrderItems.setItemId(itemId);
            subOrderItems.setItemName(items.getItemName());
            subOrderItems.setItemImg(imgUrl);
            subOrderItems.setBuyCounts(buyCount);
            subOrderItems.setItemSpecId(itemSpecId);
            subOrderItems.setItemSpecName(itemsSpec.getName());
            subOrderItems.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItems);

            // 2.4 在用户提交库存以后，扣除库存
            itemService.decreaseItemSpecStock(itemSpecId, buyCount);
        }
        order.setTotalAmount(totalAmount);
        order.setRealPayAmount(realPayAmount);

        ordersMapper.insert(order);

        // 3. 保存订单状态表
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        orderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(orderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);

        // 测试 价格更改为0.01
//        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setAmount(1);

        merchantOrdersVO.setPayMethod(payMethod);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setRemoveList(removeShopcartList);

        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);

        List<OrderStatus> orderStatusList = orderStatusMapper.select(orderStatus);

        for (OrderStatus os : orderStatusList) {
            int i = DateUtil.daysBetween(os.getCreatedTime(), new Date());
            if (i >= 1) {
                // 超过一天，关闭订单
                os.setOrderStatus(OrderStatusEnum.CLOSE.type);
                os.setCloseTime(new Date());
                orderStatusMapper.updateByPrimaryKeySelective(os);
            }
        }
    }

    private ShopcartBO getBuyCounts (List<ShopcartBO> shopcartBOList, String specId) {
        for (ShopcartBO sbo : shopcartBOList) {
            if (sbo.getSpecId().equals(specId)) {
                return sbo;
            }
        }
        return null;
    }
}