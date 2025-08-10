package com.itheima.service;

import com.itheima.pojo.CatCategory;
import java.util.List;

public interface CatCategoryService {
    void add(CatCategory catCategory);          // 新增分类
    List<CatCategory> list();                   // 查询列表
    CatCategory findById(Integer id);           // 根据ID查询
    void update(CatCategory catCategory);       // 更新分类
    void deleteById(Integer id);                // 删除分类
}