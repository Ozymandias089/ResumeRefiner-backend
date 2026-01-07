package com.resumerefiner.resumerefinerbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession(
        maxInactiveIntervalInSeconds = 60 * 60 * 2,   // 세션 유효기간: 2시간
        redisNamespace = "resumerefiner:sessions"     // Redis key prefix
)
public class RedisSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // 쿠키 이름 (기본값: SESSION)
        serializer.setCookieName("RESUMEREFINERSESSION");

        // 프론트 도메인/환경에 맞게 조정
        serializer.setCookiePath("/");
        serializer.setSameSite("Lax");
        serializer.setUseHttpOnlyCookie(true);
        // 로컬 개발 환경은 http라면 false, 나중에 https 배포 시 true로
        serializer.setUseSecureCookie(false);

        return serializer;
    }
}
