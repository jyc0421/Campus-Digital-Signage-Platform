package com.dddd.contentservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileRecordTest {

    @Test
    void testGettersAndSetters() {
        FileRecord record = new FileRecord();
        LocalDateTime now = LocalDateTime.now();

        record.setId(1L);
        record.setUserId("user123");
        record.setOriginalName("image.png");
        record.setOssKey("uploads/user123/image.png");
        record.setUrl("https://example.com/uploads/user123/image.png");
        record.setUploadedAt(now);

        assertEquals(1L, record.getId());
        assertEquals("user123", record.getUserId());
        assertEquals("image.png", record.getOriginalName());
        assertEquals("uploads/user123/image.png", record.getOssKey());
        assertEquals("https://example.com/uploads/user123/image.png", record.getUrl());
        assertEquals(now, record.getUploadedAt());
    }
}
