package com.dddd.authservice.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dddd.authservice.dto.*;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.PasswordEncoderUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        String result = authService.register(request);
        assertEquals("Registration successful", result);
    }

    @Test
    void testRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        String result = authService.register(request);
        assertEquals("Username is already taken", result);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(PasswordEncoderUtil.encode("password"));
        user.setRole(User.Role.user);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);
        assertNotNull(response.getToken());
    }

    @Test
    void testLoginWrongPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(PasswordEncoderUtil.encode("password"));

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}