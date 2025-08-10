package com.itheima.service.impl;

import com.itheima.mapper.CatCategoryMapper;
import com.itheima.pojo.CatCategory;
import com.itheima.service.CatCategoryService;
import com.itheima.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CatCategoryServiceImpl implements CatCategoryService {
    @Autowired
    private CatCategoryMapper catCategoryMapper;

    // Service接口不变
// 实现类中只需要确保传递正确的对象即可
    @Override
    public void add(CatCategory catCategory) {
        catCategory.setCreateTime(LocalDateTime.now());
        catCategory.setUpdateTime(LocalDateTime.now());
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        catCategory.setCreateUser(userId);
        catCategoryMapper.add(catCategory); // 这里会自动处理参数映射
    }

    @Override
    public List<CatCategory> list() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return catCategoryMapper.list(userId);
    }

    @Override
    public CatCategory findById(Integer id) {
        return catCategoryMapper.findById(id);
    }

    @Override
    public void update(CatCategory catCategory) {
        catCategory.setUpdateTime(LocalDateTime.now());
        catCategoryMapper.update(catCategory);
    }

    @Override
    public void deleteById(Integer id) {
        catCategoryMapper.deleteById(id);
    }
}