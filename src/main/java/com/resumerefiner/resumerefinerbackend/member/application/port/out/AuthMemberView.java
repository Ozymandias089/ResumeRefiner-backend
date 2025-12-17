package com.resumerefiner.resumerefinerbackend.member.application.port.out;

import java.time.Instant;

public interface AuthMemberView {
    Long getId();
    String getHandle();
    String getEmail();
    String getName();
    String getRole();             // "USER", "ADMIN" 등
    boolean isActive();
    String getProfileImageUrl();
    Integer getCredits();
    Instant getCreditUpdatedAt();
    Integer getResumeCount();
    Integer getReviewCount();
    Instant getCreatedAt();
}
