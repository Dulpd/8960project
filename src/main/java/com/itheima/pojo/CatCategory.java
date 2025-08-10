package com.itheima.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CatCategory {
    @NotNull(groups = Update.class)
    private Integer id;

    @NotEmpty(groups = Add.class, message = "分类名称不能为空")
    private String catCategoryName;  // 修改为 categoryName（去掉前缀）

    @NotEmpty(groups = Add.class, message = "分类别名不能为空")
    private String catCategoryAlias; // 修改为 categoryAlias

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public interface Add extends Default {}
    public interface Update extends Default {}
}