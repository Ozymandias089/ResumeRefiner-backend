package com.resumerefiner.resumerefinerbackend.review.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.internal.ReviewListItemDTO;
import lombok.Builder;

import java.util.Optional;

public interface ReviewQueryUseCase {
    Optional<ReviewListItemDTO> get(GetLatestReviewQuery query);

    @Builder
    record GetLatestReviewQuery(Handle handle, ResumeSlug slug) {}
}
