package com.dddd.contentservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UploadResponseTest {

    @Test
    void testAllArgsConstructorAndGettersSetters() {
        UploadResponse response = new UploadResponse("https://example.com/file.png", 123L);

        assertEquals("https://example.com/file.png", response.getUrl());
        assertEquals(123L, response.getFileId());

        response.setUrl("https://new-url.com");
        response.setFileId(456L);

        assertEquals("https://new-url.com", response.getUrl());
        assertEquals(456L, response.getFileId());
    }
}
