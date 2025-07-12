package com.dddd.contentservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=mysupersecuresecretkeymysupersecure"
})
@ActiveProfiles("test")
class JwtUtilTest {

    private final JwtUtil jwtUtil;

    JwtUtilTest(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("user1", "ROLE_USER", 123L);
        assertNotNull(token);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);
        assertEquals("ROLE_USER", claims.get("role"));
        assertEquals(123, ((Number) claims.get("userId")).longValue());
        assertEquals("user1", claims.getSubject());
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.value";

        Exception exception = assertThrows(Exception.class, () -> {
            jwtUtil.validateTokenAndGetClaims(invalidToken);
        });

        assertNotNull(exception.getMessage());
    }
}

