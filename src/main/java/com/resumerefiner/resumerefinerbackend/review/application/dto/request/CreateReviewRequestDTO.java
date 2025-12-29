package com.resumerefiner.resumerefinerbackend.review.application.dto.request;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.CareerStage;
import jakarta.validation.constraints.Size;

public record CreateReviewRequestDTO(
        ReviewTone tone,                 // nullable이면 기본값(예: PROFESSIONAL)
        CareerStage careerStage,         // nullable이면 UNKNOWN
        @Size(max = 200000)
        String customizationRequestJson  // nullable
) {}
