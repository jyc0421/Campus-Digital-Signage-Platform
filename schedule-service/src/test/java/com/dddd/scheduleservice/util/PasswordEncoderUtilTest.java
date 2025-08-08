package com.dddd.scheduleservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderUtilTest {

    @Test
    void testEncodeAndMatch() {
        String raw = "MySecurePassword123";
        String encoded = PasswordEncoderUtil.encode(raw);

        assertNotNull(encoded);
        assertNotEquals(raw, encoded); // 加密后不同于原文
        assertTrue(PasswordEncoderUtil.matches(raw, encoded));
    }

    @Test
    void testMatchesWithWrongPassword() {
        String raw = "correctPassword";
        String encoded = PasswordEncoderUtil.encode(raw);

        assertFalse(PasswordEncoderUtil.matches("wrongPassword", encoded));
    }

    @Test
    void testMatchesWithNullRawPassword() {
        String encodedPassword = PasswordEncoderUtil.encode("password");

        // 验证传入 null 会抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordEncoderUtil.matches(null, encodedPassword);
        });
    }

    @Test
    void testEncodeNotNullEvenWithEmptyString() {
        String encoded = PasswordEncoderUtil.encode("");
        assertNotNull(encoded);
        assertTrue(PasswordEncoderUtil.matches("", encoded));
    }
}
