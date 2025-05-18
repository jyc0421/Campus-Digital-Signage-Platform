package com.dddd.scheduleservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "schedule_content")
@Data
public class ScheduleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "order_no")
    private Integer orderNo;
}