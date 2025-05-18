package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    // 默认 findAllById(List<Long> ids) 已支持
}