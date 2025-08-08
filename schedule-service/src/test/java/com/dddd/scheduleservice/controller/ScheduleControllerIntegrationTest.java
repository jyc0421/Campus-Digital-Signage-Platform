package com.dddd.scheduleservice.controller;

import com.dddd.scheduleservice.dto.*;
import com.dddd.scheduleservice.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getAttribute("userId")).thenReturn(123L);
    }

    @Test
    void testCreateSchedule() {
        CreateScheduleRequest req = new CreateScheduleRequest();
        when(scheduleService.createSchedule(req, 123L)).thenReturn("777");

        ResponseEntity<String> response = scheduleController.createSchedule(req, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("id: 777"));
    }

    @Test
    void testGetUserSchedules() {
        ScheduleSummaryResponse summary = new ScheduleSummaryResponse();
        summary.setId(1L);
        summary.setName("Test Schedule");

        Page<ScheduleSummaryResponse> page = new PageImpl<>(List.of(summary));
        when(scheduleService.listSchedulesByUser(123L, 0, 10)).thenReturn(page);

        Page<ScheduleSummaryResponse> result = scheduleController.getUserSchedules(0, 10, request);

        assertEquals(1, result.getContent().size());
        assertEquals("Test Schedule", result.getContent().get(0).getName());
    }

    @Test
    void testGetScheduleDetail() {
        ScheduleDetailResponse detail = new ScheduleDetailResponse();
        detail.setId(1L);
        detail.setName("Detail Schedule");

        when(scheduleService.getScheduleDetail(1L, 123L)).thenReturn(detail);

        ScheduleDetailResponse result = scheduleController.getScheduleDetail(1L, request);

        assertEquals("Detail Schedule", result.getName());
    }

    @Test
    void testDeleteSchedule() {
        when(scheduleService.deleteSchedule(1L, 123L)).thenReturn("Deleted");

        String result = scheduleController.deleteSchedule(1L, request);

        assertEquals("Deleted", result);
    }

    @Test
    void testUpdateSchedule() {
        UpdateScheduleRequest req = new UpdateScheduleRequest();
        when(scheduleService.updateSchedule(1L, req, 123L)).thenReturn("Updated");

        ResponseEntity<String> response = scheduleController.updateSchedule(1L, req, request);

        assertEquals("Updated", response.getBody());
    }

    @Test
    void testGetPlayCommand() {
        PlayCommandDto dto = new PlayCommandDto(1L, 120L);
        when(scheduleService.getCurrentPlayCommand(88L)).thenReturn(dto);

        ResponseEntity<ApiResponse<PlayCommandDto>> response = scheduleController.getPlayCommand(88L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getData());
        assertEquals(1L, response.getBody().getData().getScheduleId());
        assertEquals(120L, response.getBody().getData().getDuration());
    }
}
