package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setToggle(boolean value) {
        try {
            Field field = FileController.class.getDeclaredField("toggle");
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUploadSuccess() throws IOException {
        setToggle(true);

        when(request.getAttribute("userId")).thenReturn(123L);
        UploadResponse mockResponse = new UploadResponse("http://oss.com/f1.jpg", 999L);
        when(fileService.upload(file, "123")).thenReturn(mockResponse);

        ApiResponse<UploadResponse> result = fileController.uploadFile(file, request);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("http://oss.com/f1.jpg", result.getData().getUrl());
    }

    @Test
    void testUploadRejectedForCompliance() throws IOException {
        setToggle(false); // 不合规，拦截

        when(request.getAttribute("userId")).thenReturn(123L);

        ApiResponse<UploadResponse> result = fileController.uploadFile(file, request);

        assertEquals(400, result.getCode());
        assertEquals("❌ 内容不合规", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testUploadWithNullUserId() {
        when(request.getAttribute("userId")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            fileController.uploadFile(file, request);
        });

        assertTrue(ex.getMessage().contains("userId 为 null"));
    }

    @Test
    void testListFiles() {
        when(request.getAttribute("userId")).thenReturn(123L);

        FileRecord rec = new FileRecord();
        rec.setUserId("123");
        rec.setOriginalName("x.png");

        when(fileService.getFilesByUser("123")).thenReturn(Collections.singletonList(rec));

        List<FileRecord> result = fileController.list(request);

        assertEquals(1, result.size());
        assertEquals("x.png", result.get(0).getOriginalName());
    }

    @Test
    void testDeleteFile() {
        when(request.getAttribute("userId")).thenReturn(123L);
        when(fileService.deleteFile(1L, "123")).thenReturn("File deleted");

        String result = fileController.deleteFile(1L, request);

        assertEquals("File deleted", result);
    }
}
