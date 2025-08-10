package com.itheima.service.impl;

import com.itheima.mapper.FeatureMapper;
import com.itheima.mapper.StrayCatMapper;
import com.itheima.pojo.Feature;
import com.itheima.pojo.StrayCat;
import com.itheima.service.StrayCatService;
import com.itheima.utils.ImageFeatureComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StrayCatServiceImpl implements StrayCatService {
    @Autowired
    private StrayCatMapper strayCatMapper;
    @Autowired
    private FeatureMapper featureMapper;

    @Override
    public StrayCat findByCatId(Integer catId) {
        return strayCatMapper.findByCatId(catId);
    }

    @Override
    public void add(StrayCat strayCat) {
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

        System.out.println(strayCat.getCatId());

        featureMapper.add(addFeature);

    }

    @Override
    public void update(StrayCat strayCat) {
        strayCat.setUpdateTime(LocalDateTime.now());
        strayCatMapper.update(strayCat);
    }

    @Override
    public void delete(Integer catId) {
        strayCatMapper.delete(catId);
    }

    @Override
    public List<StrayCat> findAll() {
        return strayCatMapper.findAll();
    }

    @Override
    public List<StrayCat> findByPaths(String paths) {
        System.out.println("----" + paths);
        return strayCatMapper.findByPaths(paths);
    }
}