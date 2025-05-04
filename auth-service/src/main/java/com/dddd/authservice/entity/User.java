package com.dddd.authservice.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String contactName;
    private String phone;
    private String companyName;
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.expired;

    private LocalDateTime subscriptionEnd;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Role {
        admin, user
    }

    public enum SubscriptionStatus {
        active, expired
    }
}