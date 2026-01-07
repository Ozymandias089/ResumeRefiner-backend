package com.resumerefiner.resumerefinerbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.web")
public class WebAppProperties {

    /**
     * e.g. http://localhost:3000 or https://resume-refiner-web.vercel.app
     */
    private String baseUrl;

    /**
     * CORS allowed origins (pattern allowed) e.g.
     * - http://localhost:3000
     * - https://resume-refiner-web.vercel.app
     * - https://resume-refiner-web-*.vercel.app
     */
    private List<String> allowedOriginPatterns = new ArrayList<>();

    private String oauthSuccessPath = "/oauth/success";
    private String oauthFailurePath = "/oauth/failure";

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<String> getAllowedOriginPatterns() { return allowedOriginPatterns; }
    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) { this.allowedOriginPatterns = allowedOriginPatterns; }

    public String getOauthSuccessPath() { return oauthSuccessPath; }
    public void setOauthSuccessPath(String oauthSuccessPath) { this.oauthSuccessPath = oauthSuccessPath; }

    public String getOauthFailurePath() { return oauthFailurePath; }
    public void setOauthFailurePath(String oauthFailurePath) { this.oauthFailurePath = oauthFailurePath; }

    public String oauthSuccessUrl() {
        return normalizeBaseUrl(baseUrl) + oauthSuccessPath;
    }

    public String oauthFailureUrl() {
        return normalizeBaseUrl(baseUrl) + oauthFailurePath;
    }

    private static String normalizeBaseUrl(String v) {
        if (v == null) return "";
        return v.endsWith("/") ? v.substring(0, v.length() - 1) : v;
    }
}
