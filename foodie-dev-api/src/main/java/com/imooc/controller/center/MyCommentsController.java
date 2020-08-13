package com.imooc.controller.center;

import com.imooc.config.CommonConfig;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentsService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller.center
 * @date 2020/8/2 20:49
 */
@RestController
@RequestMapping("mycomments")
public class MyCommentsController {

    @Autowired
    private MyCommentsService myCommentsService;

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "评价", notes = "评价", httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(@RequestParam String userId,
                                 @RequestParam String orderId) {
        // 判断用户和订单是否关联
        IMOOCJSONResult imoocjsonResult = checkUserOrder(orderId, userId);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return  imoocjsonResult;
        }
        // 判断该笔订单是否评价
        Orders orders = (Orders) imoocjsonResult.getData();
        if (orders.getIsComment() == YesOrNo.YES.type) {
            return IMOOCJSONResult.errorMsg("该笔订单已经评价过");
        }

        List<OrderItems> orderItems = myCommentsService.queryPendingComment(orderId);

        return IMOOCJSONResult.ok(orderItems);
    }

    @ApiOperation(value = "保存评价列表", notes = "保存评价列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(@RequestParam String userId,
                                   @RequestParam String orderId,
                                    @RequestBody List<OrderItemsCommentBO> commentList) {

        // 判断用户和订单是否关联
        IMOOCJSONResult imoocjsonResult = checkUserOrder(orderId, userId);
        if (imoocjsonResult.getStatus() != HttpStatus.OK.value()) {
            return  imoocjsonResult;
        }

        if (commentList == null || commentList.isEmpty()) {
            return IMOOCJSONResult.errorMsg("评论内容不能为空");
        }

        myCommentsService.saveComments(orderId, userId, commentList);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(
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

        PagedGridResult grid = myCommentsService.queryMyComments(userId,
                page,
                pageSize);

        return IMOOCJSONResult.ok(grid);
    }

    private  IMOOCJSONResult checkUserOrder(String orderId, String userId){
        Orders order = myOrdersService.queryMyOrder(orderId, userId);
        if (null == order) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(order);
    }
}
