package com.dddd.contentservice.controller;

import com.dddd.contentservice.dto.ApiResponse;
import com.dddd.contentservice.dto.UploadResponse;
import com.dddd.contentservice.entity.FileRecord;
import com.dddd.contentservice.service.FileService;
import com.dddd.contentservice.util.GptContentChecker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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

    @Mock
    private GptContentChecker contentChecker;

    @InjectMocks
    private FileController fileController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
    }

    @Test
    void uploadFile_success_textImageVideo_allPass() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "mockData".getBytes());

        when(contentChecker.checkText(any())).thenReturn("合规");
        when(contentChecker.checkImage(any())).thenReturn("合规");
        when(contentChecker.checkVideo(any())).thenReturn("合规");
        when(fileService.upload(any(MultipartFile.class), eq("1")))
                .thenReturn(new UploadResponse("http://mock.url", 123L));

        ApiResponse<UploadResponse> response = fileController.uploadFile(file, request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("http://mock.url", response.getData().getUrl());
    }

    @Test
    void uploadFile_fail_text() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "bad.txt", "text/plain", "mockData".getBytes());

        when(contentChecker.checkText(any())).thenReturn("违规");

        ApiResponse<UploadResponse> response = fileController.uploadFile(file, request);

        assertEquals(400, response.getCode());
        assertTrue(response.getMessage().contains("文件名不合规"));
    }

    @Test
    void uploadFile_fail_image() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "mockData".getBytes());

        when(contentChecker.checkText(any())).thenReturn("合规");
        when(contentChecker.checkImage(any())).thenReturn("违规：低俗");

        ApiResponse<UploadResponse> response = fileController.uploadFile(file, request);

        assertEquals(400, response.getCode());
        assertTrue(response.getMessage().contains("图片内容不合规"));
    }

    @Test
    void uploadFile_fail_video() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "mockData".getBytes());

        when(contentChecker.checkText(any())).thenReturn("合规");
        when(contentChecker.checkVideo(any())).thenReturn("违规：暴力");

        ApiResponse<UploadResponse> response = fileController.uploadFile(file, request);

        assertEquals(400, response.getCode());
        assertTrue(response.getMessage().contains("视频内容不合规"));
    }

    @Test
    void list_success() {
        FileRecord mockRecord = new FileRecord();
        mockRecord.setId(1L);
        mockRecord.setUserId("1");
        when(fileService.getFilesByUser("1")).thenReturn(List.of(mockRecord));

        List<FileRecord> result = fileController.list(request);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getUserId());
    }

    @Test
    void delete_success() {
        when(fileService.deleteFile(1L, "1")).thenReturn("File deleted");

        String result = fileController.deleteFile(1L, request);

        assertEquals("File deleted", result);
    }
}