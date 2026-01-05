package com.resumerefiner.resumerefinerbackend.review.application.dto.internal;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReviewListItemDTO(
        long reviewId,
        String title,
        String slug,
        long resumeVersion,
        Instant createdAt
) {}
