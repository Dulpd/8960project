package com.itheima.service;

import com.itheima.pojo.Category;
import com.itheima.pojo.User;

import java.util.List;

public interface CategoryService {


    //新增分类
    void add(Category category);

    //列表查询
    List<Category> list();

    //根据id查询分类信息
    Category findbyId(Integer id);

    //更新分类
    void update(Category category);

    void deleteById(Integer id);
}
