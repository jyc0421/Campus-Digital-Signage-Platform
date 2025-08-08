package com.dddd.authservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // 手动注入 secret 字段（绕过 Spring 注入）
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "testsecretkeytestsecretkey123456"); // 32位以上密钥

        // 手动初始化 secretKey
        jwtUtil.init();
    }

    @Test
    void testGenerateTokenAndValidate() {
        String token = jwtUtil.generateToken("testUser", "admin", 100L);
        assertNotNull(token);
        assertTrue(token.length() > 10);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertEquals("testUser", claims.getSubject());
        assertEquals("admin", claims.get("role"));
        assertEquals(100, ((Number) claims.get("userId")).longValue());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }

    @Test
    void testTokenClaimsMissingOrInvalidToken() {
        // 伪造 token（仅测试异常是否抛出）
        String fakeToken = "invalid.token.structure";

        assertThrows(Exception.class, () -> {
            jwtUtil.validateTokenAndGetClaims(fakeToken);
        });
    }
}
