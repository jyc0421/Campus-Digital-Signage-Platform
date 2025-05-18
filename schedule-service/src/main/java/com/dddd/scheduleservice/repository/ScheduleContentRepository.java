package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.ScheduleContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleContentRepository extends JpaRepository<ScheduleContent, Long> {

    // 获取调度绑定的所有内容
    List<ScheduleContent> findByScheduleId(Long scheduleId);

    // 获取某个调度绑定的内容数量
    int countByScheduleId(Long scheduleId);

    void deleteAllByScheduleId(Long scheduleId);
}