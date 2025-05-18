package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.SchedulePanel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulePanelRepository extends JpaRepository<SchedulePanel, Long> {

    // 获取调度绑定的所有面板
    List<SchedulePanel> findByScheduleId(Long scheduleId);

    // 获取绑定面板数量
    int countByScheduleId(Long scheduleId);

    void deleteAllByScheduleId(Long scheduleId);
}