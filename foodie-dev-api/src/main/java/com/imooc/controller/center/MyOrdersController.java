package com.imooc.controller.center;

import com.imooc.config.CommonConfig;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.OrdersBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrdersService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller
 * @date 2020/7/25 9:37
 */
@Api(value = "用户中心订单相关", tags = {"用户中心订单相关API"})
@RestController
@RequestMapping("myorders")
public class MyOrdersController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(@RequestParam String userId,
                                 @RequestParam Integer orderStatus,
                                 @RequestParam Integer page,
                                 @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("用户id不能为空");
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = CommonConfig.COMMENT_PAGE_SIZE;
        }

        PagedGridResult gridResult = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return IMOOCJSONResult.ok(gridResult);
    }

    // 商家发货没有后端，所以这个接口仅仅只是用于模拟
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "POST")
    @PostMapping("/deliver")
    public IMOOCJSONResult query(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return IMOOCJSONResult.errorMsg("orderId不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public IMOOCJSONResult confirmReceive(@RequestParam String orderId, @RequestParam String userId) {
        IMOOCJSONResult imoocjsonResult = checkUserOrder(orderId, userId);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return  imoocjsonResult;
        }

        boolean b = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!b){
            return IMOOCJSONResult.errorMsg("确认收货失败");
        }

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(@RequestParam String orderId, @RequestParam String userId) {
        IMOOCJSONResult imoocjsonResult = checkUserOrder(orderId, userId);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return  imoocjsonResult;
        }

        boolean b = myOrdersService.deleteOrder(userId, orderId);
        if (!b){
            return IMOOCJSONResult.errorMsg("订单删除失败");
        }
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "获得订单状态数", notes = "获得订单状态数", httpMethod = "POST")
    @PostMapping("/statusCounts")
    public IMOOCJSONResult statusCounts(@RequestParam String userId) {

        OrderStatusCountsVO orderStatusCounts = myOrdersService.getOrderStatusCounts(userId);

        return IMOOCJSONResult.ok(orderStatusCounts);
    }

    @ApiOperation(value = "查询订单动向", notes = "查询订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public IMOOCJSONResult trend(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = CommonConfig.COMMENT_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.getOrdersTrend(userId,
                page,
                pageSize);

        return IMOOCJSONResult.ok(grid);
    }

    private  IMOOCJSONResult checkUserOrder(String orderId, String userId){
        System.out.println("------"+orderId+" "+userId);
        Orders order = myOrdersService.queryMyOrder(orderId, userId);
        if (null == order) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(order);
    }

}
