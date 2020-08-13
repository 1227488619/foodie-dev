package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service.impl
 * @date 2020/7/14 8:45
 */
@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveStu() {
        saveStu1();
//        int i = 1/0;
        saveStu2();
    }
//    @Transactional(propagation = Propagation.REQUIRED)
    public void saveStu1() {
        Stu stu = new Stu();
        stu.setName("jack1");
        int i = 1/0;
        stu.setAge(19);
        stuMapper.insert(stu);
    }
//    @Transactional(propagation = Propagation.REQUIRED)
    public void saveStu2() {
        Stu stu = new Stu();
        stu.setName("jack2");
        stu.setAge(19);
        stuMapper.insert(stu);
    }






    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setName("jack");
        stu.setAge(20);
        stu.setId(id);
        stuMapper.updateByPrimaryKey(stu);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }
}
