package com.resumerefiner.resumerefinerbackend.review.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewDetailResponseDTO;
import lombok.Builder;

public interface GetReviewDetailUseCase {
    GetReviewDetailResponseDTO get(GetReviewDetailQuery query);

    @Builder
    record GetReviewDetailQuery(Handle handle, long reviewId) {}
}
