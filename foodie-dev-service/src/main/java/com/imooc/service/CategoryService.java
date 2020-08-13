package com.imooc.service;

import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/15 16:02
 */
public interface CategoryService {
    // 查询所有一级分类
    public List<Category> queryAllRootLevelCat();

    // 根据一级分类查询子分类信息
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    //查询首页下每个一级分类下的6条最新商品数据
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
