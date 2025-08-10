package com.itheima.service;

import com.itheima.pojo.StrayCat;
import java.util.List;

public interface StrayCatService {
    // 根据ID查询流浪猫信息
    StrayCat findByCatId(Integer catId);

    // 添加流浪猫信息
    void add(StrayCat strayCat);

    // 更新流浪猫信息
    void update(StrayCat strayCat);

    // 删除流浪猫信息
    void delete(Integer catId);

    // 查询所有流浪猫信息
    List<StrayCat> findAll();

    // 通过路径查询流浪猫信息
    List<StrayCat> findByPaths(String paths);
}