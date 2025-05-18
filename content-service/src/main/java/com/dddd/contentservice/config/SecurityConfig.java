package com.dddd.contentservice.config;

import com.dddd.contentservice.config.JwtUserIdInjectionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtUserIdInjectionFilter jwtUserIdInjectionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/files/upload").authenticated()
                        .anyRequest().permitAll()
                )
                // 注册 JWT Token 解析过滤器
                .addFilterBefore(jwtUserIdInjectionFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}