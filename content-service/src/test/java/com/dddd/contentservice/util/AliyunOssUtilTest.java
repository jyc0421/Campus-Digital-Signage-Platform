package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AliyunOssUtilTest {

    @Mock
    private AliyunOssConfig config;

    @Mock
    private OSS ossClient;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private OSSClientBuilder ossClientBuilder;

    private AliyunOssUtil aliyunOssUtil;

    @BeforeEach
    void setUp() throws IOException {
        // 直接注入 mock 的 builder
        aliyunOssUtil = new AliyunOssUtil(config, ossClientBuilder);

        when(config.getEndpoint()).thenReturn("https://oss-cn-shanghai.aliyuncs.com");
        when(config.getAccessKeyId()).thenReturn("testKeyId");
        when(config.getAccessKeySecret()).thenReturn("testSecret");
        when(config.getBucketName()).thenReturn("test-bucket");

        when(ossClientBuilder.build(anyString(), anyString(), anyString()))
                .thenReturn(ossClient);

        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("fake".getBytes()));
    }

    @Test
    void testUploadFile_success() throws IOException {
        String url = aliyunOssUtil.uploadFile(multipartFile, "123");
        assertTrue(url.contains("uploads/123/"));
        verify(ossClient).putObject((String) eq("test-bucket"), contains("uploads/123/"), (InputStream) any());
        verify(ossClient).shutdown();
    }

    @Test
    void testDeleteFile_success() {
        aliyunOssUtil.deleteFile("uploads/123/file.jpg");
        verify(ossClient).deleteObject("test-bucket", "uploads/123/file.jpg");
        verify(ossClient).shutdown();
    }
}

