package com.itheima.mapper;

import com.itheima.pojo.CatCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CatCategoryMapper {
    // 新增
    @Insert("INSERT INTO cat_category(cat_category_name, cat_category_alias, create_user, create_time, update_time) " +
            "VALUES(#{catCategoryName}, #{catCategoryAlias}, #{createUser}, #{createTime}, #{updateTime})")
    void add(CatCategory catCategory);

    // 查询当前用户的分类列表
    @Select("SELECT * FROM cat_category WHERE create_user = #{userId}")
    List<CatCategory> list(Integer userId);

    // 根据ID查询
    @Select("SELECT * FROM cat_category WHERE id = #{id}")
    CatCategory findById(Integer id);

    // 更新
    @Update("UPDATE cat_category SET cat_category_name=#{catCategoryName}, cat_category_alias=#{catCategoryAlias}, " +
            "update_time=#{updateTime} WHERE id=#{id}")
    void update(CatCategory catCategory);


    // 删除
    @Delete("DELETE FROM cat_category WHERE id = #{id}")
    void deleteById(Integer id);
}