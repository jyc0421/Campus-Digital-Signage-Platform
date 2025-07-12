package com.dddd.contentservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置 secret 值
        ReflectionTestUtils.setField(jwtUtil, "secret", "12345678901234567890123456789012"); // 32位 secret
        jwtUtil.init(); // 初始化 secretKey
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("alice", "admin", 1001L);
        assertNotNull(token);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);
        assertEquals("admin", claims.get("role"));
        assertEquals(1001, ((Number) claims.get("userId")).longValue());
        assertEquals("alice", claims.getSubject());
        assertTrue(claims.getExpiration().after(new java.util.Date()));
    }
}
