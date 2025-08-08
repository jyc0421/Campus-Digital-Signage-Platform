package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AliyunOssUtilTest {

    @InjectMocks
    private AliyunOssUtil aliyunOssUtil;

    @Mock
    private AliyunOssConfig config;

    @Mock
    private OSS mockOssClient;

    @Mock
    private MultipartFile file;

    @Spy
    private OSSClientBuilder ossClientBuilder = new OSSClientBuilder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(config.getAccessKeyId()).thenReturn("fake-id");
        when(config.getAccessKeySecret()).thenReturn("fake-secret");
        when(config.getEndpoint()).thenReturn("oss-cn-beijing.aliyuncs.com");
        when(config.getBucketName()).thenReturn("my-bucket");
    }

    @Test
    void testUploadFile() throws IOException {
        // 准备 mock 文件输入流
        InputStream inputStream = new ByteArrayInputStream("fake file content".getBytes());
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenReturn(inputStream);

        // 替换 createOssClient 返回的真实对象
        AliyunOssUtil spyUtil = Mockito.spy(aliyunOssUtil);
        doReturn(mockOssClient).when(spyUtil).createOssClient();

        String url = spyUtil.uploadFile(file, "123");

        verify(mockOssClient).putObject(eq("my-bucket"), contains("uploads/123/"), eq(inputStream));
        verify(mockOssClient).shutdown();

        assertTrue(url.startsWith("https://my-bucket.oss-cn-beijing.aliyuncs.com/uploads/123/"));
        assertTrue(url.endsWith(".jpg"));
    }

    @Test
    void testDeleteFile() {
        AliyunOssUtil spyUtil = Mockito.spy(aliyunOssUtil);
        doReturn(mockOssClient).when(spyUtil).createOssClient();

        spyUtil.deleteFile("uploads/123/fake.jpg");

        verify(mockOssClient).deleteObject("my-bucket", "uploads/123/fake.jpg");
        verify(mockOssClient).shutdown();
    }
}
