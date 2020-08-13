package com.imooc.config;

import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.config
 * @date 2020/7/26 18:59
 */
public class CommonConfig {

    public static final Integer COMMENT_PAGE_SIZE = 10;
    public static final String SHOPCART = "shopcart";
    public static final String REDIS_USER_TOKEN = "redis_user_token";

    // 微信支付成功 -> 支付中心 -> 天天吃货平台
    //                       |-> 回调通知的url
    public static final String PAY_RETURN_URL = "http://8g4rvd.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 支付中心的调用地址
    public static final String PAYMENT_URL = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "project" + File.separator + "images";


}
