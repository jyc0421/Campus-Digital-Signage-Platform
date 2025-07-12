package com.dddd.contentservice.service;

import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.repository.FileRecordRepository;
import com.dddd.contentservice.util.AliyunOssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private AliyunOssUtil aliyunOssUtil;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpload() throws Exception {
        String userId = "123";
        String filename = "test.txt";
        String url = "https://bucket.endpoint/uploads/123/uuid-test.txt";

        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(aliyunOssUtil.uploadFile(mockFile, userId)).thenReturn(url);
        when(fileRecordRepository.save(any())).thenAnswer(invocation -> {
            FileRecord record = invocation.getArgument(0);
            record.setId(999L); // 模拟数据库生成ID
            return record;
        });

        UploadResponse response = fileService.upload(mockFile, userId);

        assertEquals(url, response.getUrl());
        assertEquals(999L, response.getFileId());
    }

    @Test
    void testGetFilesByUser() {
        String userId = "123";
        FileRecord record = new FileRecord();
        record.setId(1L);
        record.setUserId(userId);
        record.setUrl("https://xxx");

        when(fileRecordRepository.findByUserId(userId)).thenReturn(List.of(record));

        List<FileRecord> result = fileService.getFilesByUser(userId);

        assertEquals(1, result.size());
        assertEquals("https://xxx", result.get(0).getUrl());
    }

    @Test
    void testDeleteFile_success() {
        Long id = 1L;
        String userId = "123";

        FileRecord record = new FileRecord();
        record.setId(id);
        record.setUserId(userId);
        record.setOssKey("uploads/123/file.txt");

        when(fileRecordRepository.findById(id)).thenReturn(Optional.of(record));

        String result = fileService.deleteFile(id, userId);

        assertEquals("File deleted", result);
        verify(aliyunOssUtil).deleteFile("uploads/123/file.txt");
        verify(fileRecordRepository).deleteById(id);
    }

    @Test
    void testDeleteFile_fileNotFound() {
        when(fileRecordRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(1L, "123"));
        assertEquals("File not found", ex.getMessage());
    }

    @Test
    void testDeleteFile_unauthorized() {
        FileRecord record = new FileRecord();
        record.setId(1L);
        record.setUserId("other-user");

        when(fileRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(1L, "123"));
        assertEquals("Unauthorized", ex.getMessage());
    }
}
