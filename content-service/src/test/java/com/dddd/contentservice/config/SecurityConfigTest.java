package com.dddd.contentservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testSecurityFilterChain() throws Exception {
        JwtUserIdInjectionFilter filter = mock(JwtUserIdInjectionFilter.class);
        SecurityConfig config = new SecurityConfig();
        config.jwtUserIdInjectionFilter = filter; // 手动注入 mock

        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(http);
        when(http.build()).thenReturn((DefaultSecurityFilterChain) mock(SecurityFilterChain.class));

        SecurityFilterChain chain = config.securityFilterChain(http);
        assertNotNull(chain);
    }
}
