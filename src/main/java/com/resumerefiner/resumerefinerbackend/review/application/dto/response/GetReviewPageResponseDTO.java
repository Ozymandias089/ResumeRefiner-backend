package com.resumerefiner.resumerefinerbackend.review.application.dto.response;

import com.resumerefiner.resumerefinerbackend.review.application.dto.internal.ReviewListItemDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetReviewPageResponseDTO(
        List<ReviewListItemDTO> reviews,
        int page,
        int size,
        long totalElements,
        boolean hasPrev,
        boolean hasNext
) {}
