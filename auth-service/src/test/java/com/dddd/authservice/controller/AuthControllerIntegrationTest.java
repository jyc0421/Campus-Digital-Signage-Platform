package com.dddd.authservice.controller;

import com.dddd.authservice.dto.LoginRequest;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.PasswordEncoderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private PasswordEncoderUtil passwordEncoderUtil;

    @BeforeEach
    void setup() {
        // 每次测试前都插入一个测试用户
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPasswordHash(PasswordEncoderUtil.encode("password123"));
        user.setRole(User.Role.admin);

        userRepository.deleteAll(); // 清除旧数据
        userRepository.save(user); // 插入新用户
    }

    @Test
    void testLoginSuccess() {

        LoginRequest request = new LoginRequest("testuser", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
    }

    @Test
    void testLoginFail_WrongPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}