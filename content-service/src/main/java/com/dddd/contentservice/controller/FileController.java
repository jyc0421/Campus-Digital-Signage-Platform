package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;
//
//    @PostMapping("/upload")
//    public UploadResponse uploadFile(@RequestParam("file") MultipartFile file,
//                                     HttpServletRequest request) throws IOException {
//        Long userId = (Long) request.getAttribute("userId");
//        return fileService.upload(file, String.valueOf(userId));
//    }


    @PostMapping("/upload")
    public ApiResponse<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                  HttpServletRequest request) throws IOException {
        Object uidAttr = request.getAttribute("userId");
        Long userId = uidAttr instanceof Long ? (Long) uidAttr : Long.parseLong(uidAttr.toString());
        UploadResponse response = fileService.upload(file, String.valueOf(userId));
        return ApiResponse.success(response); // ✅ 使用统一包装
    }

    @GetMapping
    public List<FileRecord> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return fileService.getFilesByUser(String.valueOf(userId));
    }

    @DeleteMapping("/{id}")
    public String deleteFile(@PathVariable Long id,
                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return fileService.deleteFile(id, String.valueOf(userId));
    }
}