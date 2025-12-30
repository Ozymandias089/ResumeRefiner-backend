package com.resumerefiner.resumerefinerbackend.review.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewDetailResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.GetReviewDetailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewDetailController {
    private final GetReviewDetailUseCase getReviewDetailUseCase;

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<GetReviewDetailResponseDTO> getReviewDetail(
            @PathVariable long reviewId,
            @AuthenticatedMember Handle handle
    ) {
        GetReviewDetailResponseDTO response = getReviewDetailUseCase.get(
                GetReviewDetailUseCase.GetReviewDetailQuery.builder()
                        .handle(handle)
                        .reviewId(reviewId)
                        .build()
        );
        return ResponseEntity.ok(response);
    }
}
