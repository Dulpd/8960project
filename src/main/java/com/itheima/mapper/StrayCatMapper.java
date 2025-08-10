package com.itheima.mapper;

import com.itheima.pojo.Article;
import com.itheima.pojo.StrayCat;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StrayCatMapper {
    // 根据ID查询流浪猫信息
    @Select("SELECT * FROM straycats WHERE cat_id = #{catId}")
    StrayCat findByCatId(Integer catId);

    // 添加流浪猫信息
    @Insert("INSERT INTO straycats (name, gender, age, breed, health_status, location_found, date_found, adoption_status, adopter_id, created_at, updated_at, cat_img) " +
            "VALUES (#{name}, #{gender}, #{age}, #{breed}, #{healthStatus}, #{locationFound}, #{dateFound}, #{adoptionStatus}, #{adopterId}, #{createTime}, #{updateTime}, #{catImg})")
    void add(StrayCat strayCat);

    // 更新流浪猫信息
    @Update("UPDATE straycats SET name=#{name}, gender=#{gender}, age=#{age}, breed=#{breed}, health_status=#{healthStatus}, " +
            "location_found=#{locationFound}, date_found=#{dateFound}, adoption_status=#{adoptionStatus}, adopter_id=#{adopterId}, " +
            "updated_at=#{updateTime}, cat_img=#{catImg} WHERE cat_id=#{catId}")
    void update(StrayCat strayCat);

    // 删除流浪猫信息
    @Delete("DELETE FROM straycats WHERE cat_id=#{catId}")
    void delete(Integer catId);

    // 查询所有流浪猫信息
    @Select("SELECT * FROM straycats")
    List<StrayCat> findAll();
    //List<StrayCat> list(Integer userId, Integer catId, String state);

    @Select("SELECT * FROM straycats WHERE FIND_IN_SET(cat_img, #{paths})")
    List<StrayCat> findByPaths(@RequestParam String paths);
}