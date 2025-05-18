package com.dddd.contentservice.repository;

import com.dddd.contentservice.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByUserId(String userId);
}