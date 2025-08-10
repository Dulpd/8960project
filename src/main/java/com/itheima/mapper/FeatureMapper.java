package com.itheima.mapper;


import com.itheima.pojo.Feature;
import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeatureMapper {
    //根据用户名查询用户
    @Select("SELECT * FROM features")
    List<Feature> list();

    //添加
    @Insert("insert into features(filename,hist_blue,hist_green,hist_red,hog_features,orb_descriptors,lbp_hist, cat_id)" +
    "VALUES (#{filename}, #{histBlue}, #{histGreen}, #{histRed}, #{hogFeatures}, #{orbDescriptors}, #{lbpHist}, #{catId})")
    void add(Feature feature);

}
