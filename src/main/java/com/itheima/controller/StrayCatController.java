package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.pojo.StrayCat;
import com.itheima.service.StrayCatService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cat")
@Validated
public class StrayCatController {
    @Autowired
    private StrayCatService strayCatService;

    // 添加流浪猫信息
    @PostMapping("/add")
    public Result add(@RequestBody @Validated StrayCat strayCat) {
        strayCatService.add(strayCat);
        return Result.success();
    }

    // 更新流浪猫信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated StrayCat strayCat) {
        strayCatService.update(strayCat);
        return Result.success();
    }

    // 删除流浪猫信息
    @DeleteMapping("/delete/{catId}")
    public Result delete(@PathVariable Integer catId) {
        strayCatService.delete(catId);
        return Result.success();
    }

    // 根据ID查询流浪猫信息
    @GetMapping("/{catId}")
    public Result<StrayCat> findByCatId(@PathVariable Integer catId) {
        StrayCat strayCat = strayCatService.findByCatId(catId);
        return Result.success(strayCat);
    }

    // 查询所有流浪猫信息
    @GetMapping("/all")
    public Result<List<StrayCat>> findAll() {
        List<StrayCat> strayCats = strayCatService.findAll();
        return Result.success(strayCats);
    }

    @GetMapping("/path")
    public Result<List<StrayCat>> findBypaths(@RequestParam String paths) {
        List<StrayCat> strayCat = strayCatService.findByPaths(paths);
        System.out.println("data:" + strayCat);
        return Result.success(strayCat);
    }
}