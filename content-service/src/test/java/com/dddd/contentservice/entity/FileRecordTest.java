package com.dddd.contentservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileRecordTest {

    @Test
    void testGettersAndSetters() {
        FileRecord record = new FileRecord();

        Long id = 1L;
        String userId = "123";
        String originalName = "file.txt";
        String ossKey = "uploads/123/file.txt";
        String url = "https://bucket.oss-cn-region.aliyuncs.com/uploads/123/file.txt";
        LocalDateTime uploadedAt = LocalDateTime.now();

        record.setId(id);
        record.setUserId(userId);
        record.setOriginalName(originalName);
        record.setOssKey(ossKey);
        record.setUrl(url);
        record.setUploadedAt(uploadedAt);

        assertEquals(id, record.getId());
        assertEquals(userId, record.getUserId());
        assertEquals(originalName, record.getOriginalName());
        assertEquals(ossKey, record.getOssKey());
        assertEquals(url, record.getUrl());
        assertEquals(uploadedAt, record.getUploadedAt());
    }

    @Test
    void testNoArgsConstructor() {
        FileRecord record = new FileRecord();
        assertNotNull(record);
    }
}
