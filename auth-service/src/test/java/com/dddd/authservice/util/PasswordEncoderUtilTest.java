package com.dddd.authservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderUtilTest {

    @Test
    void testEncode_NotNullAndDifferent() {
        String raw = "mypassword";
        String encoded = PasswordEncoderUtil.encode(raw);

        assertNotNull(encoded);
        assertNotEquals(raw, encoded); // 加密后不能与原文一致
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    void testMatches_SuccessfulMatch() {
        String raw = "securePassword";
        String encoded = PasswordEncoderUtil.encode(raw);

        assertTrue(PasswordEncoderUtil.matches(raw, encoded));
    }

    @Test
    void testMatches_FailureOnWrongPassword() {
        String raw = "password123";
        String encoded = PasswordEncoderUtil.encode(raw);

        assertFalse(PasswordEncoderUtil.matches("wrongPassword", encoded));
    }

    @Test
    void testMatchesWithNullRawPassword_ThrowsException() {
        String encoded = PasswordEncoderUtil.encode("password");

        // 修正点：断言 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordEncoderUtil.matches(null, encoded);
        });
    }

    @Test
    void testMatchesWithNullEncodedPassword_ReturnsFalse() {
        // encoded=null 实际上并不抛异常，而是 false
        assertFalse(PasswordEncoderUtil.matches("password", null));
    }
}
