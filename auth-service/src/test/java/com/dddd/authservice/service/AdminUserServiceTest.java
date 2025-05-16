package com.dddd.authservice.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dddd.authservice.dto.*;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.service.AuthService;
import com.dddd.authservice.service.AdminUserService;
import com.dddd.authservice.util.PasswordEncoderUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Collections;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccess() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newadmin");
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setRole("admin");

        when(userRepository.findByUsername("newadmin")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        String result = adminUserService.createUser(request);
        assertEquals("User created successfully", result);
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        String result = adminUserService.deleteUser(1L);
        assertEquals("User deleted successfully", result);
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(User.Role.user);
        user.setSubscriptionStatus(User.SubscriptionStatus.expired);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("updated@example.com");
        request.setRole("admin");
        request.setSubscriptionStatus("active");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = adminUserService.updateUser(1L, request);
        assertEquals("User updated successfully", result);
    }

    @Test
    void testSearchUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        user.setUsername("abc");
        user.setEmail("a@b.com");
        user.setRole(User.Role.user);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(userPage);

        Page<UserResponse> response = adminUserService.searchUsers("a", null, null, null, 0, 10);

        assertEquals(1, response.getContent().size());
        assertEquals("abc", response.getContent().get(0).getUsername());
    }
}