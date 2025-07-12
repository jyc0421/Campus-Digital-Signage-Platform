package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.dddd.contentservice.util.GptContentChecker;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    GptContentChecker contentChecker;
//
//    @PostMapping("/upload")
//    public UploadResponse uploadFile(@RequestParam("file") MultipartFile file,
//                                     HttpServletRequest request) throws IOException {
//        Long userId = (Long) request.getAttribute("userId");
//        return fileService.upload(file, String.valueOf(userId));
//    }

    /**
     * 上传文件（图片、视频、其他），并做合规性检查
     */
    @PostMapping("/upload")
    public ApiResponse<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                  HttpServletRequest request) throws IOException {
        Object uidAttr = request.getAttribute("userId");
        System.out.println("📥 Controller 收到 userId: " + uidAttr);

        if (uidAttr == null) {
            throw new RuntimeException("❌ 用户未登录，userId 为 null");
        }
        Long userId = uidAttr instanceof Long ? (Long) uidAttr : Long.parseLong(uidAttr.toString());

        // 1️⃣ 内容合规性审查
        String fileType = file.getContentType();
        String fileName = file.getOriginalFilename();

        // 默认先审查文件名（文本）
        String resultText = contentChecker.checkText(fileName);
        System.out.println("📄 文本审查：" + resultText);
        if (resultText.contains("违规")) {
            return ApiResponse.fail("❌ 文件名不合规：" + resultText);
        }

        // 图片内容审查
        if (fileType != null && fileType.startsWith("image/")) {
            String result = contentChecker.checkImage(file);
            System.out.println("🖼️ 图片审查：" + result);
            if (result.contains("违规")) {
                return ApiResponse.fail("❌ 图片内容不合规：" + result);
            }
        }

        // 视频内容审查
        if (fileType != null && fileType.startsWith("video/")) {
            String result = contentChecker.checkVideo(file);
            System.out.println("🎞️ 视频审查：" + result);
            if (result.contains("违规")) {
                return ApiResponse.fail("❌ 视频内容不合规：" + result);
            }
        }

        // 2️⃣ 合规 → 上传
        UploadResponse response = fileService.upload(file, String.valueOf(userId));
        return ApiResponse.success(response);
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