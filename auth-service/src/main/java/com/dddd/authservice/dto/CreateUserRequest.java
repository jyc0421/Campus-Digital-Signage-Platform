package com.dddd.authservice.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String contactName;
    private String phone;
    private String companyName;
    private String address;
    private String role;  // 'admin' 或 'user'
}