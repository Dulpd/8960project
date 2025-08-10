package com.itheima.mapper;

import com.itheima.pojo.Article;
import com.itheima.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper {
    // 新增
    @Insert("INSERT INTO article(title, content, cover_img, state, category_id, create_user, create_time, update_time) " +
            "VALUES (#{title}, #{content}, #{coverImg}, #{state}, #{categoryId}, #{createUser}, #{createTime}, #{updateTime})")
    void add(Article article);

    //更新
    @Update("update article set title=#{title},content=#{content},cover_img=#{coverImg},state=#{state},category_id=#{categoryId},update_time=#{updateTime} where id =#{id}")
    void update(Article article);

    //根据id删除
    @Delete("delete from article where id=#{id}")
    void deleteById(Integer id);

    List<Article> list(Integer userId, Integer categoryId, String state);
}
