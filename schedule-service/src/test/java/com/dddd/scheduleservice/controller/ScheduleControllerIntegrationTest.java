package com.dddd.scheduleservice.controller;

import com.dddd.scheduleservice.config.TestSecurityConfig;
import com.dddd.scheduleservice.dto.ContentOrderDTO;
import com.dddd.scheduleservice.dto.CreateScheduleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class) // 👈 加载上面的配置
class ScheduleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long mockUserId = 123L;

    // 👇 用于注入 userId（模拟 JwtFilter 的行为）
    private RequestPostProcessor withUserId() {
        return request -> {
            request.setAttribute("userId", mockUserId);
            return request;
        };
    }

    @Test
    void testCreateAndGetSchedule_success() throws Exception {
        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setName("Integration Test Schedule");
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusHours(1));
        request.setRepeatType("NONE");
        request.setPriority(1);
        ContentOrderDTO c1 = new ContentOrderDTO();
        c1.setContentId(101L);
        c1.setOrderNo(1);
        ContentOrderDTO c2 = new ContentOrderDTO();
        c2.setContentId(102L);
        c2.setOrderNo(2);
        request.setContents(List.of(c1, c2));
        request.setPanelIds(Collections.emptyList());

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Successfully created schedule")));    }
}