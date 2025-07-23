package com.dddd.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Long> panelIds;
}