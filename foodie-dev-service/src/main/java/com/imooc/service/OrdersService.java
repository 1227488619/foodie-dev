package com.imooc.service;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.OrdersBO;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.vo.OrderVO;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/25 9:43
 */
public interface OrdersService {

    // 创建订单
    public OrderVO createOrders(List<ShopcartBO> shopcartBOList, OrdersBO ordersBO);

    // 修改订单状态
    public void updateOrderStatus(String orderId, Integer orderStatus);

    // 查看支付状态
    public OrderStatus queryOrderStatusInfo(String orderId);

    // 关闭超时订单
    public void closeOrder();
}
