package com.resumerefiner.resumerefinerbackend.config;

import com.resumerefiner.resumerefinerbackend.global.security.MemberAuthProvider;
import com.resumerefiner.resumerefinerbackend.global.security.oauth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WebAppProperties.class)
public class SecurityConfig {

    private final MemberAuthProvider memberAuthProvider;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final WebAppProperties webAppProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/static/**",
                                "/api/health",
                                "/api/version",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/handle/check",
                                "/api/email/check",
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
                .sessionManagement(session -> session.sessionFixation().migrateSession())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            // (선택) 프런트 failure 페이지에서 reason 표시하고 싶으면 쿼리로 전달
                            String reason = exception != null ? exception.getClass().getSimpleName() : "OAuthLoginFailed";
                            String url = webAppProperties.oauthFailureUrl()
                                    + "?reason=" + URLEncoder.encode(reason, StandardCharsets.UTF_8);
                            response.sendRedirect(url);
                        })
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return new ProviderManager(memberAuthProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 핵심: 프로퍼티에서 주입
        configuration.setAllowedOriginPatterns(webAppProperties.getAllowedOriginPatterns());

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
