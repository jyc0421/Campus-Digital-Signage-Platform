package com.dddd.contentservice.service;

import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.repository.FileRecordRepository;
import com.dddd.contentservice.util.AliyunOssUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FileService {

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private AliyunOssUtil aliyunOssUtil;

    public UploadResponse upload(MultipartFile file, String userId) throws IOException {
        String url = aliyunOssUtil.uploadFile(file, userId);
        String key = url.substring(url.indexOf("uploads/")); // OSS路径

        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setOriginalName(file.getOriginalFilename());
        record.setOssKey(key);
        record.setUrl(url);
        record.setUploadedAt(LocalDateTime.now());

        fileRecordRepository.save(record);

        return new UploadResponse(url, record.getId());
    }

    public List<FileRecord> getFilesByUser(String userId) {
        return fileRecordRepository.findByUserId(userId);
    }

    public String deleteFile(Long id, String userId) {
        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        aliyunOssUtil.deleteFile(record.getOssKey());
        fileRecordRepository.deleteById(id);

        return "File deleted";
    }
}