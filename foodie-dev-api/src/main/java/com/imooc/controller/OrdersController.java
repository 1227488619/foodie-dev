package com.imooc.controller;

import com.imooc.config.CommonConfig;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.OrdersBO;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrdersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller
 * @date 2020/7/25 9:37
 */
@Api(value = "订单相关", tags = {"订单相关API"})
@RestController
@RequestMapping("orders")
public class OrdersController{

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(
            @RequestBody OrdersBO ordersBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 判断支付方式是否支持
        if (!PayMethod.WEIXIN.type.equals(ordersBO.getPayMethod()) && !PayMethod.ALIPAY.type.equals(ordersBO.getPayMethod())){
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        String shopcartJson = redisOperator.get("shopcart:" + ordersBO.getUserId());
        if (StringUtils.isNotBlank(shopcartJson)) {
            IMOOCJSONResult.errorMsg("购物车中数据有误");
        }
        List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);

        // 1. 创建订单
        OrderVO orderVO = ordersService.createOrders(shopcartBOList, ordersBO);
        String orderId = orderVO.getOrderId();


        // 2. 创建订单之后移除购物车中的已结算的商品(缓存和cookie)
        shopcartBOList.removeAll(orderVO.getRemoveList());
        redisOperator.set(CommonConfig.SHOPCART + ":" + ordersBO.getUserId(), JsonUtils.objectToJson(shopcartBOList));

        CookieUtils.setCookie(request, response, CommonConfig.SHOPCART, JsonUtils.objectToJson(shopcartBOList), true);

        // 3. 向支付中心发送当前订单
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(CommonConfig.PAY_RETURN_URL);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("imoocUserId","1227488619-4908325");
        httpHeaders.add("password", "r90j-4kj0-903i-4tt4");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, httpHeaders);

        ResponseEntity<IMOOCJSONResult> imoocjsonResultResponse = restTemplate.postForEntity(CommonConfig.PAYMENT_URL, entity,
                IMOOCJSONResult.class);

        IMOOCJSONResult paymentResult = imoocjsonResultResponse.getBody();

        if (paymentResult.getStatus() != 200) {
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败");
        }

        return IMOOCJSONResult.ok(orderId);
    }

    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(@RequestParam String merchantOrderId) {

        ordersService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);

        return HttpStatus.OK.value();
    }

    @PostMapping("/getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(@RequestParam String orderId) {

        OrderStatus orderStatus = ordersService.queryOrderStatusInfo(orderId);
        return IMOOCJSONResult.ok(orderStatus);
    }

}
