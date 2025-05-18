package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.Panel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanelRepository extends JpaRepository<Panel, Long> {
    // 可扩展按 subscriberId 查等方法
}