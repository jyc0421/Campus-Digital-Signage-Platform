package com.dddd.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleSummaryResponse {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String repeatType;
    private Integer priority;
    private Integer contentCount;
    private Integer panelCount;
}