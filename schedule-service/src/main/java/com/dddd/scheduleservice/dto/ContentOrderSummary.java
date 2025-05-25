package com.dddd.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentOrderSummary {
    private Long contentId;
    private int orderNo;
    private String originalName;
}