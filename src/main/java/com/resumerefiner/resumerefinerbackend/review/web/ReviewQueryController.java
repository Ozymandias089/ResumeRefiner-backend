package com.resumerefiner.resumerefinerbackend.review.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.internal.ReviewListItemDTO;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewPageResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.PageMyReviewsUseCase;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.ReviewQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewQueryController {
    private final PageMyReviewsUseCase pageMyReviewsUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;

    /**
     * 내 전체 리뷰 최신 목록 조회
     * GET /api/reviews?page=0&size=20
     */
    @GetMapping("/reviews")
    public ResponseEntity<GetReviewPageResponseDTO> getMyReviews(
            @AuthenticatedMember Handle handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        GetReviewPageResponseDTO response = pageMyReviewsUseCase.page(
                PageMyReviewsUseCase.PageMyReviewsQuery.builder()
                        .handle(handle)
                        .slug(Optional.empty())
                        .page(page)
                        .size(size)
                        .build()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 이력서의 리뷰 최신 목록 조회
     * GET /api/resumes/{slug}/reviews?page=0&size=20
     */
    @GetMapping("/resumes/{slug}/reviews")
    public ResponseEntity<GetReviewPageResponseDTO> getReviewsByResume(
            @PathVariable String slug,
            @AuthenticatedMember Handle handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        GetReviewPageResponseDTO response = pageMyReviewsUseCase.page(
                PageMyReviewsUseCase.PageMyReviewsQuery.builder()
                        .handle(handle)
                        .slug(Optional.of(ResumeSlug.of(slug)))
                        .page(page)
                        .size(size)
                        .build()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resumes/{slug}/reviews/latest")
    public ResponseEntity<ReviewListItemDTO> getLatestReview(
            @PathVariable String slug,
            @AuthenticatedMember Handle handle
    ) {
        Optional<ReviewListItemDTO> dtoOpt = reviewQueryUseCase.get(
                ReviewQueryUseCase.GetLatestReviewQuery.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .build()
        );

        return dtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
