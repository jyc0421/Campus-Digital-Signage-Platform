package com.dddd.contentservice.config;

import com.dddd.contentservice.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtUserIdInjectionFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            try {
//                String token = authHeader.substring(7);
//                Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//                Long userId = claims.get("userId", Long.class);
//                request.setAttribute("userId", userId);
//            } catch (Exception e) {
//                // token 解析失败也放行，但不注入 userId
//                log.warn("JWT 解析失败: {}", e.getMessage());
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);
            Long userId = claims.get("userId", Long.class);

            if (userId != null) {
                // 设置 userId 到 request 中
                request.setAttribute("userId", userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            System.out.println("JWT 解析成功，userId = " + userId);
            request.setAttribute("userId", userId);
        } catch (Exception e) {
            System.out.println("JWT 解析失败：" + e.getMessage());
        }
    } else {
        System.out.println("⚠️ 未提供 Bearer Token");
    }

    filterChain.doFilter(request, response);
}
}