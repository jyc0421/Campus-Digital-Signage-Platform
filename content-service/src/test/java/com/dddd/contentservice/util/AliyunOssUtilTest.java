package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AliyunOssUtilTest {

    @Mock
    private AliyunOssConfig config;

    @Mock
    private OSS mockOssClient;

    private AliyunOssUtil ossUtil;

    @BeforeEach
    void setUp() {
        when(config.getEndpoint()).thenReturn("https://oss-cn-shanghai.aliyuncs.com");
        when(config.getAccessKeyId()).thenReturn("testKeyId");
        when(config.getAccessKeySecret()).thenReturn("testSecret");
        when(config.getBucketName()).thenReturn("test-bucket");

        ossUtil = Mockito.spy(new AliyunOssUtil());
        ReflectionTestUtils.setField(ossUtil, "config", config);
        doReturn(mockOssClient).when(ossUtil).createOssClient();
    }

    @Test
    void testUploadFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "fake-image-content".getBytes());

        String url = ossUtil.uploadFile(file, "123");

        assertTrue(url.contains("uploads/123/"));
        verify(mockOssClient).putObject(eq("test-bucket"), contains("uploads/123/"), any(InputStream.class));
        verify(mockOssClient).shutdown();
    }

    @Test
    void testDeleteFile_success() {
        ossUtil.deleteFile("uploads/123/test.jpg");

        verify(mockOssClient).deleteObject("test-bucket", "uploads/123/test.jpg");
        verify(mockOssClient).shutdown();
    }
}
