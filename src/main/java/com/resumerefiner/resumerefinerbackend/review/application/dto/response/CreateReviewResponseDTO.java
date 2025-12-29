package com.resumerefiner.resumerefinerbackend.review.application.dto.response;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateReviewResponseDTO(
        Long id,
        Long resumeId,
        Long resumeVersion,
        int sequencePerVersion,
        String model,
        ReviewTone tone,

        Integer inputSchemaVersion,
        String inputSnapshotJson,

        Integer customRequestSchemaVersion,
        String customRequestJson,

        Integer outputSchemaVersion,
        String outputJson,

        Instant createdAt,
        Instant updatedAt
) {}
