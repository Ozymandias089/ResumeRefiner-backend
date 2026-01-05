package com.resumerefiner.resumerefinerbackend.review.application.dto.response;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import lombok.Builder;

import java.time.Instant;

@Builder
public record GetReviewDetailResponseDTO(
        long reviewId,

        // 표시용 (리스트/상세 공통)
        String title,
        String slug,

        long resumeVersion,
        int sequencePerVersion,

        // Meta
        ReviewTone tone,
        String model,
        Instant createdAt,
        Instant updatedAt,

        // input snapshot
        int inputSchemaVersion,
        String inputSnapshotJson,

        // customization request (optional)
        Integer customRequestSchemaVersion,
        String customRequestJson,

        // output snapshot
        int outputSchemaVersion,
        String outputJson
) {}
