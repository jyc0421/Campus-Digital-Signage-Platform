package com.dddd.deviceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: Panel响应数据传输对象
 * @author: Do
 * @date: 2024/3/30 14:05
 */
@Data
@AllArgsConstructor
public class PanelResponseDTO {
    /**
     * 面板ID
     */
    private Long id;
    /**
     * 面板名称
     */
    private String name;
    /**
     * 面板位置
     */
    private String location;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * MAC地址
     */
    private String macAddress;
    /**
     * 面板状态
     */
    private String status;
    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeat;
}
