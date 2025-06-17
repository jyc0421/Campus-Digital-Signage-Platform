package com.dddd.scheduleservice.config;

import com.dddd.scheduleservice.config.JwtUserIdInjectionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!test")  // ✅ 禁用在测试环境中被加载
public class SecurityConfig {

    @Autowired
    private JwtUserIdInjectionFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 关闭 CSRF（适用于 JWT）
                .authorizeHttpRequests(auth -> auth
                        // ✅ 放行 Agent 接口
                        .requestMatchers("/api/schedules/play-command").permitAll()

                        // ✅ 其他 API 要求认证
                        .requestMatchers("/api/**").authenticated()

                        // ✅ 所有非 API 请求默认放行（如 Swagger、静态资源）
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}