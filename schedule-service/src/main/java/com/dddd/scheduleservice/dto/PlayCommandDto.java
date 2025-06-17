package com.dddd.scheduleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayCommandDto {
    private Long scheduleId;
    private Long duration; // 单位：秒
}