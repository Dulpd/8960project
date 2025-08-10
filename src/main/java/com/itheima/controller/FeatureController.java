package com.itheima.controller;


import com.itheima.pojo.Feature;
import com.itheima.pojo.FeaturesResult;
import com.itheima.pojo.StrayCat;
import com.itheima.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.itheima.pojo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/feature")
public class FeatureController {
    @Autowired
    private FeatureService featureService;

    @PostMapping
    public Result add(@RequestBody @Validated(Feature.Add.class) Feature feature) {
        featureService.add(feature);
        return  Result.success();
    }

    @GetMapping
    public Result<List<Feature>> list() {
        List<Feature> features = featureService.list();
        return  Result.success(features);
    }

    // 添加特征
    @PostMapping("/add")
    public Result<List<FeaturesResult>> add(@RequestBody @Validated StrayCat strayCat) {
        List<FeaturesResult> featuresResults = featureService.addFeaturesAndCat(strayCat);
        return Result.success(featuresResults);
    }

}
