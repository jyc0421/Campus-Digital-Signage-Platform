package com.dddd.authservice.controller;

import com.dddd.authservice.config.SecurityBypassConfig;
import com.dddd.authservice.dto.CreateUserRequest;
import com.dddd.authservice.dto.UpdateUserRequest;
import com.dddd.authservice.dto.UserResponse;
import com.dddd.authservice.service.AdminUserService;

import com.dddd.authservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(AdminUserController.class)
@Import(SecurityBypassConfig.class) // 👈 引入一个测试用的安全绕过配置类
class AdminUserControllerTest {

    @MockBean
    private JwtUtil jwtUtil;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("email@example.com");
        request.setPassword("123456");
        request.setRole("admin");

        when(adminUserService.createUser(any())).thenReturn("User created successfully");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(adminUserService.deleteUser(1L)).thenReturn("User deleted successfully");

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("updated@example.com");
        request.setRole("admin");

        when(adminUserService.updateUser(eq(1L), any())).thenReturn("User updated successfully");

        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }

    @Test
    void testSearchUsers() throws Exception {
        UserResponse user = new UserResponse();
        user.setUserId(1L);
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setRole("user");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<UserResponse> page = new PageImpl<>(Collections.singletonList(user));

        when(adminUserService.searchUsers(any(), any(), any(), any(), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                        .param("username", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("test"));
    }
}
