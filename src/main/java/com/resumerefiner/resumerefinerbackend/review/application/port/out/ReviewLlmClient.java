package com.resumerefiner.resumerefinerbackend.review.application.port.out;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewCustomizationRequest;
import lombok.Builder;

public interface ReviewLlmClient {
    @Builder
    record Result(
            String model,
            int outputSchemaVersion,
            String outputJson
    ) {}

    Result generateReview(
            String inputSnapshotJson,
            int inputSchemaVersion,
            ReviewTone tone,
            ReviewCustomizationRequest customizationRequest
    );
}
