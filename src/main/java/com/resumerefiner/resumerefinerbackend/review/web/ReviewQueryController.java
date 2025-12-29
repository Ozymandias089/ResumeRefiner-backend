package com.resumerefiner.resumerefinerbackend.review.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewPageResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.PageMyReviewsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewQueryController {
    private final PageMyReviewsUseCase pageMyReviewsUseCase;

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
                PageMyReviewsUseCase.PageMyReviewsCommand.builder()
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
                PageMyReviewsUseCase.PageMyReviewsCommand.builder()
                        .handle(handle)
                        .slug(Optional.of(ResumeSlug.of(slug)))
                        .page(page)
                        .size(size)
                        .build()
        );
        return ResponseEntity.ok(response);
    }
}
