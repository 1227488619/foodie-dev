package com.imooc.service;

import com.imooc.pojo.Stu;
import org.springframework.stereotype.Service;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.service
 * @date 2020/7/14 8:43
 */

public interface StuService {
    public Stu getStuInfo(int id);
    public void saveStu();
    public void updateStu(int id);
    public void deleteStu(int id);
}
