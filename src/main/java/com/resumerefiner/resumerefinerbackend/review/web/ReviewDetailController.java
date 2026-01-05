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

    @GetMapping("/resumes/{slug}/reviews/{reviewId}")
    public ResponseEntity<GetReviewDetailResponseDTO> getReviewDetailByResume(
            @PathVariable String slug,
            @PathVariable long reviewId,
            @AuthenticatedMember Handle handle
    ) {
        // slug는 인증/소유권 검증에 필수는 아니고,
        // 응답의 slug는 서비스에서 resumeId로 조회해서 채우고 있으니 그냥 무시해도 됨.
        // (원하면 slug mismatch 검증 추가 가능)

        return ResponseEntity.ok(
                getReviewDetailUseCase.get(
                        GetReviewDetailUseCase.GetReviewDetailQuery.builder()
                                .handle(handle)
                                .reviewId(reviewId)
                                .build()
                )
        );
    }
}
