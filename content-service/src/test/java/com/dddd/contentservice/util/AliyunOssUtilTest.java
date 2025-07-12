package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AliyunOssUtilTest {

    @InjectMocks
    private AliyunOssUtil aliyunOssUtil;

    @Mock
    private AliyunOssConfig config;

    @Mock
    private OSSClientBuilder builder;

    @Mock
    private OSS ossClient;

    @BeforeEach
    void setup() {
        when(config.getEndpoint()).thenReturn("oss-cn-beijing.aliyuncs.com");
        when(config.getAccessKeyId()).thenReturn("testId");
        when(config.getAccessKeySecret()).thenReturn("testSecret");
        when(config.getBucketName()).thenReturn("test-bucket");

        // 替换 OSSClientBuilder 为 mock
        ReflectionTestUtils.setField(aliyunOssUtil, "config", config);
    }

    @Test
    void testUploadFile_success() throws IOException {
        try (MockedStatic<OSSClientBuilder> mockedBuilder = mockStatic(OSSClientBuilder.class)) {
            mockedBuilder.when(OSSClientBuilder::new).thenReturn(builder);
            when(builder.build(anyString(), anyString(), anyString())).thenReturn(ossClient);

            // 准备 mock 上传文件
            byte[] content = "hello".getBytes();
            MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", content);

            // 模拟行为
            doNothing().when(ossClient).putObject(anyString(), anyString(), (InputStream) any());

            String url = aliyunOssUtil.uploadFile(file, "user123");

            assertTrue(url.contains("test-bucket.oss-cn-beijing.aliyuncs.com"));
            verify(ossClient, times(1)).shutdown();
        }
    }

    @Test
    void testDeleteFile_success() {
        try (MockedStatic<OSSClientBuilder> mockedBuilder = mockStatic(OSSClientBuilder.class)) {
            mockedBuilder.when(OSSClientBuilder::new).thenReturn(builder);
            when(builder.build(anyString(), anyString(), anyString())).thenReturn(ossClient);

            doNothing().when(ossClient).deleteObject(anyString(), anyString());

            aliyunOssUtil.deleteFile("uploads/user123/file.txt");

            verify(ossClient, times(1)).deleteObject("test-bucket", "uploads/user123/file.txt");
            verify(ossClient, times(1)).shutdown();
        }
    }
}
