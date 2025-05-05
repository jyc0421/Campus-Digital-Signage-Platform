package com.dddd.authservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUserRequest {
    private String email;
    private String contactName;
    private String phone;
    private String companyName;
    private String address;
    private String role;  // 可选: 'admin' 或 'user'
    private String subscriptionStatus;  // 可选: 'active' 或 'expired'
    private LocalDateTime subscriptionEnd;
}