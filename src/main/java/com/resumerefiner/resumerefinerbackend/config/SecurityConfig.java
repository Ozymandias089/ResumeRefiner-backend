package com.resumerefiner.resumerefinerbackend.config;

import com.resumerefiner.resumerefinerbackend.global.security.MemberAuthProvider;
import com.resumerefiner.resumerefinerbackend.global.security.oauth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberAuthProvider memberAuthProvider;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                //.anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        // 인증 안 된 경우 → 401
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        // 인증은 됐으나 접근 불가 → 403
                        .accessDeniedHandler((req, res, ex) -> {
                            res.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 열어둘 API들
                        .requestMatchers(
                                "/static/**",
                                "/api/health",
                                "/api/version",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/handle/check",
                                "/api/email/check",

                                // Mandatory for OAuth2 features
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/profile").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/profile/password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/media/profile-image").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/media/profile-image").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/media/resume-image/{slug}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/media/resume-image/{slug}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resumes").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resumes").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resumes/{slug}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/resumes/{slug}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/resumes/{slug}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resumes/{slug}/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resumes/{slug}/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resumes/{slug}/reviews/{reviewId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/{reviewId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resumes/{slug}/reviews/latest").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/resumes/{slug}/reviews/delete").authenticated()
                        .anyRequest().authenticated()
                )
                // 세션 기반 인증을 쓸 것이므로 stateless(X)
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                )
                // 폼 로그인/기본 로그인은 안 쓰고, 우리가 만든 REST 로그인만 사용
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("http://localhost:3000/oauth/failure");
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return new ProviderManager(memberAuthProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "https://resume-refiner-web.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // withCredentials: true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
