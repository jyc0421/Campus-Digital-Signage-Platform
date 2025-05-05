package com.dddd.authservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String contactName;
    private String phone;
    private String companyName;
    private String address;
    private String role;
    private String subscriptionStatus;
    private LocalDateTime subscriptionEnd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}