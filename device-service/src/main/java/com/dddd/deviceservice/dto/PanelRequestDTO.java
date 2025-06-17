package com.dddd.deviceservice.dto;

import lombok.Data;

@Data
public class PanelRequestDTO {
    private Long subscriberId;
    private String name;
    private String location;
    private String ipAddress;
    private String macAddress;
}