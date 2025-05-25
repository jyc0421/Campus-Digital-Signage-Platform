package com.dddd.scheduleservice.controller;

import com.dddd.scheduleservice.dto.CreateScheduleRequest;
import com.dddd.scheduleservice.dto.ScheduleDetailResponse;
import com.dddd.scheduleservice.dto.ScheduleSummaryResponse;
import com.dddd.scheduleservice.dto.UpdateScheduleRequest;
import com.dddd.scheduleservice.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<String> createSchedule(@RequestBody CreateScheduleRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId"); // JWT 中注入
        String msg = scheduleService.createSchedule(request, userId);
        return ResponseEntity.ok("Successfully created schedule, id: " + msg);
    }
    @GetMapping
    public Page<ScheduleSummaryResponse> getUserSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        return scheduleService.listSchedulesByUser(userId, page, size);
    }
    @GetMapping("/{id}")
    public ScheduleDetailResponse getScheduleDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return scheduleService.getScheduleDetail(id, userId);
    }
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return scheduleService.deleteSchedule(id, userId);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSchedule(@PathVariable Long id,
                                                 @RequestBody UpdateScheduleRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String msg = scheduleService.updateSchedule(id, request, userId);
        return ResponseEntity.ok(msg);
    }
}