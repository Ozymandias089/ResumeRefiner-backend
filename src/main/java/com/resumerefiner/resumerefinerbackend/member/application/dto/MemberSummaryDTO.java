package com.resumerefiner.resumerefinerbackend.member.application.dto;

import java.time.Instant;

public record MemberSummaryDTO(
        Long id,
        String handle,
        String email,
        String name,
        String role,
        boolean isActive,
        String profileImageUrl,
        int credits,
        Instant creditUpdatedAt,
        int resumeCount,
        int reviewCount,
        Instant createdAt
) {}
