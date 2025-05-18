package com.dddd.scheduleservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subscriberId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String repeatType;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}