package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 分页获取某个订阅者的调度列表
    Page<Schedule> findBySubscriberId(Long subscriberId, Pageable pageable);

    // 查询某个用户的具体调度任务（防止越权）
    Optional<Schedule> findByIdAndSubscriberId(Long id, Long subscriberId);
}