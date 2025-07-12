package com.dddd.contentservice.config;

import com.dddd.contentservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtUserIdInjectionFilterTest {

    private JwtUserIdInjectionFilter filter;
    private JwtUtil jwtUtil;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private Claims claims;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        filter = new JwtUserIdInjectionFilter();
        filter.jwtUtil = jwtUtil;

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        claims = mock(Claims.class);
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testInvalidAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testValidTokenWithUserId() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtUtil.validateTokenAndGetClaims("valid.token")).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(123L);

        filter.doFilterInternal(request, response, chain);

        verify(request, atLeastOnce()).setAttribute("userId", 123L);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testValidTokenWithoutUserId() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer no.userid");
        when(jwtUtil.validateTokenAndGetClaims("no.userid")).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        // 改为验证它确实被调用且值为 null
        verify(request).setAttribute(eq("userId"), isNull());
        verify(chain).doFilter(request, response);
    }


    @Test
    void testTokenThrowsException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtUtil.validateTokenAndGetClaims("bad.token")).thenThrow(new RuntimeException("invalid"));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
