package com.resumerefiner.resumerefinerbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.storage.local.root-dir}")
    private String rootDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // http://localhost:8080/static/** -> {rootDir}/**
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + Paths.get(rootDir).toAbsolutePath().toString() + "/");
    }
}
