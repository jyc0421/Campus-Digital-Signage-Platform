package com.dddd.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateScheduleRequest {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String repeatType; // ONCE / DAILY / WEEKLY
    private int priority;
    private List<ContentOrderDTO> contents;
    private List<Long> panelIds;
}