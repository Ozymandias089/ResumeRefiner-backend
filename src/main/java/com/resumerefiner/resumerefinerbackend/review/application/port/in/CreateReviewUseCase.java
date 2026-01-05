package com.resumerefiner.resumerefinerbackend.review.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.CreateReviewResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.CareerStage;
import lombok.Builder;

public interface CreateReviewUseCase {

    CreateReviewResponseDTO review(CreateReviewCommand command);

    @Builder
    record CreateReviewCommand(
            Handle handle,
            ResumeSlug slug,
            ReviewTone tone,
            CareerStage stage,
            String customizationRequestJson
    ){}
}
