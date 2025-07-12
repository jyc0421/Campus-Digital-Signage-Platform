package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

class AliyunOssUtilTest {

    private AliyunOssConfig config;
    private OSS ossMock;
    private AliyunOssUtil util;

    @BeforeEach
    void setup() {
        config = mock(AliyunOssConfig.class);
        when(config.getEndpoint()).thenReturn("oss-cn-beijing.aliyuncs.com");
        when(config.getAccessKeyId()).thenReturn("fakeId");
        when(config.getAccessKeySecret()).thenReturn("fakeSecret");
        when(config.getBucketName()).thenReturn("test-bucket");

        util = new AliyunOssUtil();
        util.config = config;

        ossMock = mock(OSS.class);
    }

    @Test
    void testUploadFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        try (MockedStatic<OSSClientBuilder> mockedStatic = mockStatic(OSSClientBuilder.class)) {
            OSSClientBuilder builder = mock(OSSClientBuilder.class);
            mockedStatic.when(OSSClientBuilder::new).thenReturn(builder);
            when(builder.build(anyString(), anyString(), anyString())).thenReturn(ossMock);

            doNothing().when(ossMock).putObject(anyString(), anyString(), (InputStream) any());
            doNothing().when(ossMock).shutdown();

            util.uploadFile(file, "user123");
        }
    }

    @Test
    void testDeleteFile_success() {
        try (MockedStatic<OSSClientBuilder> mockedStatic = mockStatic(OSSClientBuilder.class)) {
            OSSClientBuilder builder = mock(OSSClientBuilder.class);
            mockedStatic.when(OSSClientBuilder::new).thenReturn(builder);
            when(builder.build(anyString(), anyString(), anyString())).thenReturn(ossMock);

            doNothing().when(ossMock).deleteObject(anyString(), anyString());
            doNothing().when(ossMock).shutdown();

            util.deleteFile("uploads/user123/abc.txt");
        }
    }
}
