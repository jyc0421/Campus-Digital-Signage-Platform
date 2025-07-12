package com.dddd.contentservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // 用反射设置 private 字段 secret，并初始化 secretKey
        var secret = "mysupersecuresecretkeymysupersecure";
        var secretField = assertDoesNotThrow(() -> JwtUtil.class.getDeclaredField("secret"));
        secretField.setAccessible(true);
        assertDoesNotThrow(() -> secretField.set(jwtUtil, secret));

        // 调用 @PostConstruct 初始化 secretKey
        jwtUtil.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("user1", "ADMIN", 123L);
        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertEquals("user1", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertEquals(123, claims.get("userId", Integer.class));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(Exception.class, () -> jwtUtil.validateTokenAndGetClaims(invalidToken));
    }
}

