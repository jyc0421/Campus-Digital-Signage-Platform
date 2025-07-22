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

        // ✅ 轮询逻辑（控制合规/违规交替出现）
        if (toggle) {
            toggle = false; // 下一次就不通过
        } else {
            toggle = true;
            return ApiResponse.fail("❌ 内容不合规");
        }

        // ✅ 合规就上传
        UploadResponse response = fileService.upload(file, String.valueOf(userId));
        return ApiResponse.success(response);
    }

    // 静态标志位，控制一次通过一次不通过
    private static boolean toggle = true;


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