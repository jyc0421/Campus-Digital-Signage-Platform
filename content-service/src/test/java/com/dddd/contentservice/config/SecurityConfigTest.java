package com.dddd.contentservice.config;

import com.dddd.contentservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "jwt.secret=mysupersecuresecretkeymysupersecure"
})
@ActiveProfiles("test")
@Import({SecurityConfig.class, JwtUserIdInjectionFilter.class, JwtUtil.class})
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void testSecurityFilterChainLoads() {
        assertThat(securityFilterChain).isNotNull();
    }
}


