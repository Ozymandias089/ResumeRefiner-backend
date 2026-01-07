package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ResumeSummaryDTO(
        String slug,
        String title,
        Instant createdAt,
        Instant updatedAt,
        long reviewCount
) {}
