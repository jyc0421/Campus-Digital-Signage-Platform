package com.dddd.scheduleservice.util;

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

        // 使用测试密钥（长度需符合 HMAC-SHA 要求，建议 >=256bit）
        String testSecret = "test-secret-key-for-jwt-token-signing-which-is-long-enough";
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);

        // 手动调用 @PostConstruct 方法初始化 key
        jwtUtil.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("felix", "admin", 999L);
        assertNotNull(token);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertEquals("felix", claims.getSubject());
        assertEquals("admin", claims.get("role"));
        assertEquals(999, ((Number) claims.get("userId")).intValue());
        assertTrue(claims.getExpiration().after(new java.util.Date()));
    }

    @Test
    void testInvalidTokenThrowsException() {
        String invalidToken = "invalid.token.string";
        assertThrows(Exception.class, () -> {
            jwtUtil.validateTokenAndGetClaims(invalidToken);
        });
    }
}
