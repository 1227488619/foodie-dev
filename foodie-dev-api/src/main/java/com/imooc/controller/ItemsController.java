package com.imooc.controller;

import com.imooc.config.CommonConfig;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemInfoVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller
 * @date 2020/7/15 19:16
 */
@Api(value = "商品接口", tags = {"商品信息展示接口"})
@RestController
@RequestMapping("items")
public class ItemsController{

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(@PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        // 将查出来的商品详情放进商品详情VO
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return IMOOCJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public IMOOCJSONResult commentsLevel(
            @ApiParam(name = "itemId",value = "商品ID", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        CommentLevelCountsVO commentLevelCountsVO = itemService.queryCommentsCounts(itemId);

        return IMOOCJSONResult.ok(commentLevelCountsVO);
    }

    @ApiOperation(value = "查询商品评论详情", notes = "查询商品评论详情", httpMethod = "GET")
    @GetMapping("/comments")
    public IMOOCJSONResult comments(
            @ApiParam(name = "itemId",value = "商品ID", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value = "评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page",value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return IMOOCJSONResult.errorMsg("");
        }

        // 设置page的默认值
        if (page == null) {
            page = 1;
        }
        // 设置pageSize的默认值
        if (pageSize == null) {
            pageSize = CommonConfig.COMMENT_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.queryPagedComments(itemId, level, page, pageSize);

        return IMOOCJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords",value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort",value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        // 设置page的默认值
        if (page == null) {
            page = 1;
        }
        // 设置pageSize的默认值
        if (pageSize == null) {
            pageSize = CommonConfig.COMMENT_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.searchItems(keywords, sort, page, pageSize);

        return IMOOCJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "通过分类ID搜索商品列表", notes = "通过分类ID搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId",value = "分类ID", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort",value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (catId == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        // 设置page的默认值
        if (page == null) {
            page = 1;
        }
        // 设置pageSize的默认值
        if (pageSize == null) {
            pageSize = CommonConfig.COMMENT_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = itemService.searchItems(catId, sort, page, pageSize);

        return IMOOCJSONResult.ok(pagedGridResult);
    }

    // 用于用户长时间未登录，刷新购物车中的数据，主要用于更新商品价格
    @ApiOperation(value = "通过分类ID搜索商品列表", notes = "通过分类ID搜索商品列表", httpMethod = "GET")
    @GetMapping("/refresh")
    public IMOOCJSONResult refresh(
            @ApiParam(name = "itemSpecIds",value = "拼接的规格ID", required = true)
            @RequestParam String itemSpecIds) {

        if (itemSpecIds == null) {
            return IMOOCJSONResult.errorMsg(null);
        }

        List<ShopcartVO> shopcartVOList = itemService.queryItemsBySpecIds(itemSpecIds);

        return IMOOCJSONResult.ok(shopcartVOList);
    }

}
