package com.dddd.contentservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UploadResponseTest {

    @Test
    void testAllArgsConstructorAndGettersAndSetters() {
        String url = "https://oss.com/123.jpg";
        Long fileId = 123L;

        UploadResponse response = new UploadResponse(url, fileId);

        // 验证字段值
        assertEquals(url, response.getUrl());
        assertEquals(fileId, response.getFileId());

        // 验证 setter
        response.setUrl("https://oss.com/456.png");
        response.setFileId(456L);

        assertEquals("https://oss.com/456.png", response.getUrl());
        assertEquals(456L, response.getFileId());
    }
}
