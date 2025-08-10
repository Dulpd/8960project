package com.itheima.pojo;

import lombok.Data;

import java.util.List;

@Data
public class FeaturesResult {
    private String imageName;

    private double similarity;

    private List<String> similarImages;

    private List<StrayCat> similarStrayCatList;
}
