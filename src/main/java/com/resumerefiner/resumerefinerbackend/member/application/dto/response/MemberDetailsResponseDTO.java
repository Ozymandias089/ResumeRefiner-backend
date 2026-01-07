package com.resumerefiner.resumerefinerbackend.member.application.dto.response;

import lombok.Builder;

import java.time.Instant;

public record MemberDetailsResponseDTO(
        Long id,
        String handle,
        String email,
        String name,
        String role,
        boolean isActive,

        String provider,
        String providerUserId,

        String profileImageUrl,
        int credits,
        Instant creditUpdatedAt,
        int resumeCount,
        int reviewCount,
        Instant createdAt,
        Instant updatedAt
) {
    @Builder
    public MemberDetailsResponseDTO{}
}
