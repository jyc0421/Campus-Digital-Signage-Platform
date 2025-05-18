package com.dddd.scheduleservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "schedule_panel")
@Data
public class SchedulePanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "panel_id", nullable = false)
    private Long panelId;
}