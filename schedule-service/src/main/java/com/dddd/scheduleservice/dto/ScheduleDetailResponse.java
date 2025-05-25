package com.dddd.scheduleservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleDetailResponse {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String repeatType;
    private Integer priority;
    private List<ContentItem> contents;
    private List<PanelItem> panels;

    @Data
    public static class ContentItem {
        private Long id;
        private String originalName;
        private String url;
        private Integer orderNo; // 新增字段：播放顺序
    }

    @Data
    public static class PanelItem {
        private Long id;
        private String name;
        private String location;
    }
}