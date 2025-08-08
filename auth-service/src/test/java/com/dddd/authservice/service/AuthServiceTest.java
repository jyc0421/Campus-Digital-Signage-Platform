package com.dddd.authservice.service;

import com.dddd.authservice.dto.LoginRequest;
import com.dddd.authservice.dto.LoginResponse;
import com.dddd.authservice.dto.RegisterRequest;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.exception.BusinessException;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.JwtUtil;
import com.dddd.authservice.util.PasswordEncoderUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 注册成功
    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("123456");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        String result = authService.register(request);

        assertEquals("Registration successful", result);
        verify(userRepository).save(any(User.class));
    }

    // 注册失败：用户名重复
    @Test
    void testRegisterUsernameTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        String result = authService.register(request);
        assertEquals("Username is already taken", result);
    }

    // 注册失败：邮箱重复
    @Test
    void testRegisterEmailTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("exist@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        String result = authService.register(request);
        assertEquals("Email is already registered", result);
    }

    // 登录成功
    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("123456");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setRole(User.Role.user);
        user.setPasswordHash(PasswordEncoderUtil.encode("123456")); // 密码模拟加密

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("testuser", "user", 1L)).thenReturn("token123");

        LoginResponse response = authService.login(request);
        assertEquals("token123", response.getToken());
    }

    // 登录失败：用户不存在
    @Test
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nouser");
        request.setPassword("123");

        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals("User not found", ex.getMessage());
    }

    // 登录失败：密码错误
    @Test
    void testLoginPasswordMismatch() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setUsername("user");
        user.setPasswordHash(PasswordEncoderUtil.encode("correctpassword")); // 正确密码是别的

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid credentials", ex.getMessage());
    }
}
