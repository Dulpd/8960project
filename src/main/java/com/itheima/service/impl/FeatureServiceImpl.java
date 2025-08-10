package com.itheima.service.impl;

import com.itheima.mapper.FeatureMapper;
import com.itheima.mapper.StrayCatMapper;
import com.itheima.pojo.Feature;
import com.itheima.pojo.FeaturesResult;
import com.itheima.pojo.StrayCat;
import com.itheima.service.FeatureService;
import com.itheima.utils.ImageFeatureComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    private FeatureMapper featureMapper;

    @Autowired
    private StrayCatMapper strayCatMapper;

    @Override
    public void add(Feature feature) {
        featureMapper.add(feature);
    }

    @Override
    public List<Feature> list() {
        return featureMapper.list();
    }

    @Override
    public List<FeaturesResult> addFeaturesAndCat(StrayCat strayCat) {
// 设置默认领养状态为"待领养"
        if (strayCat.getAdoptionStatus() == null || strayCat.getAdoptionStatus().isEmpty()) {
            strayCat.setAdoptionStatus("待领养");
        }
        strayCat.setCreateTime(LocalDateTime.now());
        strayCat.setUpdateTime(LocalDateTime.now());
        strayCatMapper.add(strayCat);

        Feature feature = new Feature();

        feature.setFilename(strayCat.getCatImg());
        feature.setCatId(strayCat.getCatId());

        Feature addFeature = ImageFeatureComparator.analyzeAndSaveNewImage(feature);

        featureMapper.add(addFeature);

//        ImageFeatureComparator.analyzeAndSaveNewImage(feature);

        List<Feature> featuresList = featureMapper.list();

        List<FeaturesResult> featuresResList = ImageFeatureComparator.compareAndFindSimilarImages(addFeature, featuresList);

        int listLen = featuresResList.size();

        List<String> featuresPathList = featuresResList.get(listLen - 1).getSimilarImages();

//        for(String featurePath : featuresPathList) {
//            featurePath = "http://localhost:5173/api/img/" + featurePath;
//        }

        for (int i = 0; i < featuresPathList.size(); i++) {
            String newPath = "http://localhost:5173/api/img/" + featuresPathList.get(i);
            featuresPathList.set(i, newPath);
        }

        String paths = String.join(",", featuresPathList);

        System.out.println("Select:"+paths);

        List<StrayCat> strayCatList = strayCatMapper.findByPaths(paths);

        System.out.println(strayCatList);

        featuresResList.get(listLen - 1).setSimilarStrayCatList(strayCatList);

        return featuresResList;
    }

}
