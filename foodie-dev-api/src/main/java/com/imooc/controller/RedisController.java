package com.imooc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller
 * @date 2020/8/6 10:50
 */

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/get")
    public String get(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/set")
    public String set(String key, String value){
        redisTemplate.opsForValue().set(key, value);
        return "ok";
    }

    @GetMapping("/del")
    public String del(String key){
        redisTemplate.delete(key);
        return "ok";
    }
}
