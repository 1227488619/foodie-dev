package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/15 18:19
 */
public interface ItemService {

    // 根据商品ID查详情
    public Items queryItemById(String itemId);

    // 根据商品ID查询图片列表
    public List<ItemsImg> queryItemImgList(String itemId);

    // 根据商品ID查询商品规格
    public List<ItemsSpec> queryItemSpecList(String itemId);

    // 根据商品ID查询商品参数
    public ItemsParam queryItemParam(String itemId);

    // 根据商品查询商品的评价等级数量
    public CommentLevelCountsVO queryCommentsCounts(String itemId);

    // 根据商品ID 查询商品的评价（分页）
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    // 搜索商品列表
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    // 根据分类ID搜索商品列表
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);

    // 根据规格ids查询最新的购物车中商品数据，用于刷新渲染购物车中的商品数据
    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    // 根据规格id查询具体信息
    public ItemsSpec queryItemBySpecId(String specId);

    // 根据商品id，获得商品主图url
    public String queryItemMainImgById(String itemId);

    // 减少库存
    public void decreaseItemSpecStock(String specId, int buyCount);
}
