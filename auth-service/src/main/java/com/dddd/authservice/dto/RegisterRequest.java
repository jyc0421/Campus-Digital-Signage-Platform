package com.dddd.authservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String contactName;
    private String phone;
    private String companyName;
    private String address;
}