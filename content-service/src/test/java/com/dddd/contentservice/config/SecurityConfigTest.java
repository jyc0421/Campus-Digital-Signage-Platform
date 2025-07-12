package com.dddd.contentservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void testSecurityFilterChainLoads() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(SecurityConfig.class, JwtUserIdInjectionFilter.class);

        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
        });
    }
}


