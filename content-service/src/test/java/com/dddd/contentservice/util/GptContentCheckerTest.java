package com.dddd.contentservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GptContentCheckerTest {

    private GptContentChecker checker;
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setUp() {
        checker = new GptContentChecker();
        mockRestTemplate = mock(RestTemplate.class);

        // 用 Spring 工具注入私有字段
        ReflectionTestUtils.setField(checker, "openaiApiKey", "dummy-key");
        ReflectionTestUtils.setField(GptContentChecker.class, "restTemplate", mockRestTemplate);
    }

    @Test
    void testCheckText_validResponse() {
        Map<String, Object> mockResp = Map.of(
                "choices", List.of(Map.of("message", "合规"))
        );
        ResponseEntity<Map> entity = new ResponseEntity<>(mockResp, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(entity);

        String result = checker.checkText("测试文本");
        assertTrue(result.contains("合规"));
    }

    @Test
    void testCheckText_nullChoices() {
        Map<String, Object> mockResp = Map.of();
        ResponseEntity<Map> entity = new ResponseEntity<>(mockResp, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(entity);

        String result = checker.checkText("空响应");
        assertTrue(result.contains("模型返回空"));
    }

    @Test
    void testCheckText_exceptionThrown() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("error"));

        String result = checker.checkText("异常测试");
        assertTrue(result.contains("文本放行："));
    }

    @Test
    void testCheckImage_valid() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("mockBytes".getBytes());

        Map<String, Object> mockResp = Map.of(
                "choices", List.of(Map.of("message", "合规"))
        );
        ResponseEntity<Map> entity = new ResponseEntity<>(mockResp, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(entity);

        String result = checker.checkImage(file);
        assertTrue(result.contains("合规"));
    }

    @Test
    void testCheckImage_ioException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("读取失败"));

        String result = checker.checkImage(file);
        assertTrue(result.contains("图片读取失败"));
    }

    @Test
    void testCheckImageBytes_valid() {
        Map<String, Object> mockResp = Map.of(
                "choices", List.of(Map.of("message", "合规"))
        );
        ResponseEntity<Map> entity = new ResponseEntity<>(mockResp, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(entity);

        String base64 = Base64.getEncoder().encodeToString("mock".getBytes());
        String result = checker.checkImageBytes(base64);
        assertTrue(result.contains("合规"));
    }

    @Test
    void testCheckImageBytes_exception() {
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("imageError"));

        String base64 = Base64.getEncoder().encodeToString("mock".getBytes());
        String result = checker.checkImageBytes(base64);
        assertTrue(result.contains("图片放行"));
    }

    @Test
    void testCheckVideo_validFile() {
        byte[] data = "mock video".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", data);
        String result = checker.checkVideo(file);
        assertTrue(result.contains("合规"));
    }

    @Test
    void testCheckVideo_nullFile() {
        String result = checker.checkVideo(null);
        assertTrue(result.contains("⚠️ 视频为空"));
    }

    @Test
    void testCheckVideo_emptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[0]);
        String result = checker.checkVideo(file);
        assertTrue(result.contains("⚠️ 视频为空"));
    }
}
