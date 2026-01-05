package com.resumerefiner.resumerefinerbackend.review.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewPageResponseDTO;
import lombok.Builder;

import java.util.Optional;

public interface PageMyReviewsUseCase {
    GetReviewPageResponseDTO page(PageMyReviewsCommand command);

    @Builder
    record PageMyReviewsCommand(
            Handle handle,
            Optional<ResumeSlug> slug, // 있으면 이력서별, 없으면 전체
            int page,
            int size
    ) {}
}
