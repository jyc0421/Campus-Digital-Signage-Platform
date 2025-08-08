package com.dddd.contentservice.service;

import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.repository.FileRecordRepository;
import com.dddd.contentservice.util.AliyunOssUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private AliyunOssUtil aliyunOssUtil;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpload() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        String userId = "user123";
        String url = "https://bucket.oss-cn.aliyun.com/uploads/fake-key.jpg";

        when(mockFile.getOriginalFilename()).thenReturn("file.jpg");
        when(aliyunOssUtil.uploadFile(mockFile, userId)).thenReturn(url);

        FileRecord savedRecord = new FileRecord();
        savedRecord.setId(123L); // 假设数据库保存后返回的ID
        when(fileRecordRepository.save(any(FileRecord.class))).thenAnswer(invocation -> {
            FileRecord arg = invocation.getArgument(0);
            arg.setId(123L);
            return arg;
        });

        UploadResponse response = fileService.upload(mockFile, userId);

        assertEquals(url, response.getUrl());
    }

    @Test
    void testGetFilesByUser() {
        String userId = "user123";
        FileRecord record = new FileRecord();
        record.setUserId(userId);
        record.setOriginalName("file.jpg");
        record.setUrl("https://example.com/file.jpg");

        when(fileRecordRepository.findByUserId(userId)).thenReturn(Collections.singletonList(record));

        List<FileRecord> result = fileService.getFilesByUser(userId);

        assertEquals(1, result.size());
        assertEquals("file.jpg", result.get(0).getOriginalName());
    }

    @Test
    void testDeleteFileSuccess() {
        Long fileId = 1L;
        String userId = "user123";

        FileRecord record = new FileRecord();
        record.setId(fileId);
        record.setUserId(userId);
        record.setOssKey("uploads/fake-key.jpg");

        when(fileRecordRepository.findById(fileId)).thenReturn(Optional.of(record));

        String result = fileService.deleteFile(fileId, userId);

        verify(aliyunOssUtil).deleteFile("uploads/fake-key.jpg");
        verify(fileRecordRepository).deleteById(fileId);
        assertEquals("File deleted", result);
    }

    @Test
    void testDeleteFileUnauthorized() {
        Long fileId = 1L;
        String userId = "user123";
        FileRecord record = new FileRecord();
        record.setId(fileId);
        record.setUserId("otherUser");

        when(fileRecordRepository.findById(fileId)).thenReturn(Optional.of(record));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.deleteFile(fileId, userId);
        });

        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void testDeleteFileNotFound() {
        when(fileRecordRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileService.deleteFile(999L, "anyUser");
        });

        assertEquals("File not found", exception.getMessage());
    }
}
