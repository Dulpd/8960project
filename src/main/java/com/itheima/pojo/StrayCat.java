package com.itheima.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
public class StrayCat {

    private Integer catId; // 主键ID

    @NotEmpty
    private String name; // 流浪猫名字

    @NotEmpty
    private String gender; // 性别（male、female）
    private Integer age; // 年龄（月）
    private String breed; // 品种（对应前端的categoryId）
    private String healthStatus; // 健康状况
    private String locationFound; // 发现地点
    private LocalDateTime dateFound; // 发现时间

    private String adoptionStatus; // 领养状态（对应前端的state）
    private Integer adopterId; // 领养人ID
    private String description; // 新增描述字段

    @URL
    private String catImg;//封面图像
    public String getCoverImg() {
        return catImg;
    }

    public void setCoverImg(String coverImg) {
        this.catImg = coverImg;
    }
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}