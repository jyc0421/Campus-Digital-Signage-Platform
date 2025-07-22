package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    private MockHttpServletRequest request;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());
        // 重置轮询标志位
        try {
            var field = FileController.class.getDeclaredField("toggle");
            field.setAccessible(true);
            field.set(null, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUpload_passThenFail() throws IOException {
        request.setAttribute("userId", 1L);
        when(fileService.upload(any(), eq("1")))
                .thenReturn(new UploadResponse("mockUrl", 123L));

        // 第一次应上传成功
        ApiResponse<UploadResponse> res1 = fileController.uploadFile(mockFile, request);
        assertEquals(200, getCode(res1));
        assertEquals("mockUrl", res1.getData().getUrl());

        // 第二次应失败
        ApiResponse<UploadResponse> res2 = fileController.uploadFile(mockFile, request);
        assertEquals(400, getCode(res2));
        assertTrue(res2.getMessage().contains("不合规"));

        verify(fileService, times(1)).upload(any(), eq("1"));
    }

    @Test
    void testUpload_noUserId_shouldThrow() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                fileController.uploadFile(mockFile, request));
        assertTrue(ex.getMessage().contains("userId 为 null"));
    }

    @Test
    void testList_shouldReturnFiles() {
        request.setAttribute("userId", 7L);
        List<FileRecord> mockList = List.of(new FileRecord());
        when(fileService.getFilesByUser("7")).thenReturn(mockList);

        List<FileRecord> result = fileController.list(request);
        assertEquals(1, result.size());
    }

    @Test
    void testDelete_shouldWork() {
        request.setAttribute("userId", 9L);
        when(fileService.deleteFile(22L, "9")).thenReturn("done");

        String res = fileController.deleteFile(22L, request);
        assertEquals("done", res);
    }

    // 提取 code，兼容 ApiResponse 无 isSuccess 方法
    private int getCode(ApiResponse<?> resp) {
        try {
            var f = resp.getClass().getDeclaredField("code");
            f.setAccessible(true);
            return (int) f.get(resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}