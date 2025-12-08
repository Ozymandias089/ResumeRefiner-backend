package com.resumerefiner.resumerefinerbackend.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.application.name:resume-refiner-backend}")
    private String appName;

    @Value("${app.version:0.0.1}")
    private String appVersion;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "application", appName
        );
    }

    @GetMapping("/api/version")
    public Map<String, Object> version() {
        return Map.of(
                "application", appName,
                "version", appVersion
        );
    }
}
