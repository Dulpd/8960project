package com.itheima.controller;

import com.itheima.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.UUID;

@CrossOrigin
@RestController
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        //把文件内容存储到本地磁盘上
        String originalFilename= file.getOriginalFilename();
        //这样可以
        // 证名字是唯一的，防止文件被覆盖
        String filename= UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        file.transferTo(new File(uploadPath+filename));
        return Result.success("http://localhost:5173/api/img/"+filename);

    }
}
