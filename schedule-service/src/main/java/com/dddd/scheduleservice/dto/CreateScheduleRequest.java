package com.dddd.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateScheduleRequest {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String repeatType; // ONCE, DAILY, WEEKLY
    private int priority;
    private List<Long> contentIds;
    private List<Long> panelIds;
}