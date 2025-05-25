package com.dddd.contentservice.service;

import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.repository.FileRecordRepository;
import com.dddd.contentservice.util.AliyunOssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class FileServiceTest {

    private FileService fileService;
    private FileRecordRepository fileRecordRepository;
    private AliyunOssUtil aliyunOssUtil;

    @BeforeEach
    void setUp() {
        fileRecordRepository = mock(FileRecordRepository.class);
        aliyunOssUtil = mock(AliyunOssUtil.class);
        fileService = new FileService(fileRecordRepository, aliyunOssUtil);
    }

    @Test
    void testUploadFile_success() throws IOException {
        // 准备模拟数据
        String userId = "user123";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake-content".getBytes());
        String fakeUrl = "https://oss.aliyun.com/uploads/user123/test.jpg";

        // 模拟 Aliyun OSS 上传行为
        when(aliyunOssUtil.uploadFile(any(), any())).thenAnswer(invocation -> {
            return fakeUrl;
        });
        // 模拟数据库保存时自动生成 ID（模拟 JPA 行为）
        when(fileRecordRepository.save(any(FileRecord.class)))
                .thenAnswer(invocation -> {
                    FileRecord record = invocation.getArgument(0);
                    record.setId(42L); // 关键点：设置 ID
                    return record;
                });

        // 执行上传逻辑
        UploadResponse response = fileService.upload(file, userId);

        // 验证返回体
        assertEquals(fakeUrl, response.getUrl());
        assertEquals(42L, response.getFileId());

        // 验证数据库保存内容
        ArgumentCaptor<FileRecord> captor = ArgumentCaptor.forClass(FileRecord.class);
        verify(fileRecordRepository).save(captor.capture());
        FileRecord saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals("test.jpg", saved.getOriginalName());
        assertTrue(saved.getOssKey().startsWith("uploads/"));
        assertEquals(fakeUrl, saved.getUrl());
        assertNotNull(saved.getUploadedAt());
    }
}