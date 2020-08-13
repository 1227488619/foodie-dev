package com.imooc.service;

import com.imooc.pojo.Carousel;

import java.util.List;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/15 15:29
 */
public interface CarouselService {

    // 查询所有轮播图列表
    public List<Carousel> queryAll(Integer isShow);
}
