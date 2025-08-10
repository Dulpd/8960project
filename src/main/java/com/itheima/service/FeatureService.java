package com.itheima.service;

import com.itheima.pojo.Feature;
import com.itheima.pojo.FeaturesResult;
import com.itheima.pojo.StrayCat;

import java.util.List;

public interface FeatureService {
    // 获取全部特征信息
    List<Feature> list();

    // 新增特征
    void add(Feature feature);

    List<FeaturesResult> addFeaturesAndCat(StrayCat strayCat);

}
