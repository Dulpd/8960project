package com.itheima.controller;

import com.itheima.pojo.CatCategory;
import com.itheima.pojo.Result;
import com.itheima.service.CatCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/catCategory")
public class CatCategoryController {
    @Autowired
    private CatCategoryService catCategoryService;

    @PostMapping
    public Result add(@RequestBody @Validated(CatCategory.Add.class) CatCategory catCategory) {
        catCategoryService.add(catCategory);
        return Result.success();
    }

    @GetMapping
    public Result<List<CatCategory>> list() {
        List<CatCategory> list = catCategoryService.list();
        return Result.success(list);
    }

    @GetMapping("/detail")
    public Result<CatCategory> detail(Integer id) {
        CatCategory catCategory = catCategoryService.findById(id);
        return Result.success(catCategory);
    }

    @PutMapping
    public Result update(@RequestBody @Validated(CatCategory.Update.class) CatCategory catCategory) {
        catCategoryService.update(catCategory);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(Integer id) {
        catCategoryService.deleteById(id);
        return Result.success();
    }
}