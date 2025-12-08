package com.resumerefiner.resumerefinerbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 개발 초기에는 CSRF, 폼로그인, HTTP Basic 다 꺼두자
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 헬스체크, 버전, Swagger 등은 공개
                        .requestMatchers(
                                "/api/health",
                                "/api/version",
                                "/actuator/health",
                                "/actuator/info",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // 그 외는 일단 전부 허용 (나중에 세션/권한 붙일 예정)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
