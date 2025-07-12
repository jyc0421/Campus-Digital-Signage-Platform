package com.dddd.contentservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderUtilTest {

    @Test
    void testEncodeAndMatches() {
        String rawPassword = "mySecurePassword123!";
        String encoded = PasswordEncoderUtil.encode(rawPassword);

        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$")); // bcrypt 特征
        assertTrue(PasswordEncoderUtil.matches(rawPassword, encoded));
    }

    @Test
    void testMismatch() {
        String encoded = PasswordEncoderUtil.encode("original");
        assertFalse(PasswordEncoderUtil.matches("wrong", encoded));
    }
}
