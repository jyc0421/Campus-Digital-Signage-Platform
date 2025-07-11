package com.dddd.contentservice.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GptContentCheckerTest {

    @InjectMocks
    private final GptContentChecker checker = new GptContentChecker();

    @Test
    void testCheckText_safe() {
        // 只校验 fallback 返回逻辑
        String result = checker.checkText(null);
        assertNotNull(result);
        assertTrue(result.contains("文本放行"));
    }

    @Test
    void testCheckImage_safe() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "image.png", "image/png", new byte[]{1, 2, 3});
        String result = checker.checkImage(mockFile);
        assertNotNull(result);
        assertTrue(result.contains("图片放行"));
    }

    @Test
    void testCheckVideo_safe() throws Exception {
        // 构造一个假视频文件（只要能触发 fallback）
        MockMultipartFile mockVideo = new MockMultipartFile(
                "video", "video.mp4", "video/mp4", new byte[]{1, 2, 3});
        String result = checker.checkVideo(mockVideo);
        assertNotNull(result);
        assertTrue(result.contains("视频放行"));
    }

    @Test
    void testCheckImageBytes_safe() {
        String result = checker.checkImageBytes("invalid-base64-image");
        assertNotNull(result);
        assertTrue(result.contains("视频放行"));
    }

    @Test
    void testWhisperTranscribe_safe() {
        String result = checker.whisperTranscribe(new File("not_exists.mp3"));
        assertNotNull(result);
        assertTrue(result.contains("Whisper转写失败"));
    }
}
