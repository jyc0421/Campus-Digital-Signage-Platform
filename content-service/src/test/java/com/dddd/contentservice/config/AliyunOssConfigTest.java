package com.dddd.contentservice.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AliyunOssConfigTest {

    @Test
    void testGettersAndSetters() {
        AliyunOssConfig config = new AliyunOssConfig();
        config.setEndpoint("endpoint");
        config.setAccessKeyId("keyId");
        config.setAccessKeySecret("keySecret");
        config.setBucketName("bucket");

        assertEquals("endpoint", config.getEndpoint());
        assertEquals("keyId", config.getAccessKeyId());
        assertEquals("keySecret", config.getAccessKeySecret());
        assertEquals("bucket", config.getBucketName());
    }
}
