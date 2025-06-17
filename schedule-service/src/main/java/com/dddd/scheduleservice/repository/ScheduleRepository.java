package com.dddd.scheduleservice.repository;

import com.dddd.scheduleservice.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 分页获取某个订阅者的调度列表
    Page<Schedule> findBySubscriberId(Long subscriberId, Pageable pageable);

    // 查询某个用户的具体调度任务（防止越权）
    Optional<Schedule> findByIdAndSubscriberId(Long id, Long subscriberId);

    @Query("""
        SELECT s FROM Schedule s
        JOIN SchedulePanel sp ON s.id = sp.scheduleId
        WHERE sp.panelId = :panelId
        AND :now BETWEEN s.startTime AND s.endTime
        ORDER BY s.priority DESC
        LIMIT 1
    """)
    Optional<Schedule> findActiveScheduleForPanel(@Param("panelId") Long panelId,
                                                  @Param("now") LocalDateTime now);
}