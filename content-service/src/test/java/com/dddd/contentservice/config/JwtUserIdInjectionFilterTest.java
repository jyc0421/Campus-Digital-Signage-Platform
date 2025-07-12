
package com.dddd.contentservice.config;

import com.dddd.contentservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUserIdInjectionFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    @InjectMocks
    private JwtUserIdInjectionFilter filter;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidTokenWithUserId() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtUtil.validateTokenAndGetClaims("valid.token")).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(123L);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("userId", 123L);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidTokenWithoutUserId() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token");
        when(jwtUtil.validateTokenAndGetClaims("valid.token")).thenReturn(claims);
        when(claims.get("userId", Long.class)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(eq("userId"), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtUtil.validateTokenAndGetClaims("invalid.token")).thenThrow(new RuntimeException("Invalid token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(eq("userId"), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(eq("userId"), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testHeaderWithoutBearerPrefix() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(eq("userId"), any());
        verify(filterChain).doFilter(request, response);
    }
}
