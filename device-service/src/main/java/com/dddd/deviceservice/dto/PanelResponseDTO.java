package com.dddd.deviceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PanelResponseDTO {
    private Long id;
    private String name;
    private String location;
    private String ipAddress;
    private String macAddress;
    private String status;
    private LocalDateTime lastHeartbeat;
}