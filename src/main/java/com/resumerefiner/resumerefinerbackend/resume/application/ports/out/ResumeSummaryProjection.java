package com.resumerefiner.resumerefinerbackend.resume.application.ports.out;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ResumeSummaryProjection(
        ResumeSlug slug,
        String title,
        Instant createdAt,
        Instant updatedAt,
        long reviewCount
) {}
