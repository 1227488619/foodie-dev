package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller
 * @date 2020/7/14 8:49
 */

@ApiIgnore
@RestController
public class StuController {
    @Autowired
    private StuService stuService;

    @GetMapping("/get/{id}")
    public Stu getStu(@PathVariable Integer id){
        return stuService.getStuInfo(id);
    }

    @PostMapping("/save")
    public Object saveStu(){
        stuService.saveStu();
        return "ok";
    }

    @PostMapping("/update/{id}")
    public Object updateStu(@PathVariable Integer id){
        stuService.updateStu(id);
        return "ok";
    }

    @PostMapping("/delete/{id}")
    public Object deleteStu(@PathVariable Integer id){
        stuService.deleteStu(id);
        return "ok";
    }

}
