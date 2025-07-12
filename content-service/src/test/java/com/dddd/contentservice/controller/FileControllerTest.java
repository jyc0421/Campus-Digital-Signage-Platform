package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import com.dddd.contentservice.util.GptContentChecker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileController controller;
    private FileService fileService;
    private GptContentChecker checker;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        checker = mock(GptContentChecker.class);
        controller = new FileController();
        controller.fileService = fileService;
        controller.contentChecker = checker;
        request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(123L);
    }

    @Test
    void uploadFile_textViolation() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "bad.txt", "text/plain", "test".getBytes());
        when(checker.checkText("bad.txt")).thenReturn("违规：政治敏感词");

        ApiResponse<?> response = controller.uploadFile(file, request);
        assertEquals(400, response.getCode());
    }

    @Test
    void uploadFile_imageViolation() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "good.png", "image/png", new byte[1]);
        when(checker.checkText("good.png")).thenReturn("合规");
        when(checker.checkImage(file)).thenReturn("违规：色情");

        ApiResponse<?> response = controller.uploadFile(file, request);
        assertEquals(400, response.getCode());
    }

    @Test
    void uploadFile_videoViolation() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[1]);
        when(checker.checkText("video.mp4")).thenReturn("合规");
//        when(checker.checkVideo(file)).thenReturn("违规：暴力");

        ApiResponse<?> response = controller.uploadFile(file, request);
        assertEquals(400, response.getCode());
    }

    @Test
    void uploadFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "nice.txt", "text/plain", "123".getBytes());
        when(checker.checkText("nice.txt")).thenReturn("合规");
        UploadResponse uploadResponse = new UploadResponse("http://xxx", 1L);
        when(fileService.upload(file, "123")).thenReturn(uploadResponse);

        ApiResponse<UploadResponse> response = controller.uploadFile(file, request);
        assertEquals(200, response.getCode());
        assertEquals("http://xxx", response.getData().getUrl());
    }

    @Test
    void list() {
        FileRecord rec = new FileRecord();
        rec.setUserId("123");
        when(fileService.getFilesByUser("123")).thenReturn(List.of(rec));

        List<FileRecord> result = controller.list(request);
        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getUserId());
    }

    @Test
    void deleteFile() {
        when(fileService.deleteFile(1L, "123")).thenReturn("ok");

        String result = controller.deleteFile(1L, request);
        assertEquals("ok", result);
    }

    @Test
    void uploadFile_nullUser() {
        when(request.getAttribute("userId")).thenReturn(null);
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "aaa".getBytes());
        assertThrows(RuntimeException.class, () -> controller.uploadFile(file, request));
    }
}
