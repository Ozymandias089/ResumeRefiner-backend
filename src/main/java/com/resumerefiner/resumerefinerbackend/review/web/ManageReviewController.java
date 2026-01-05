package com.resumerefiner.resumerefinerbackend.review.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.ManageReviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ManageReviewController {
    private final ManageReviewUseCase manageReviewUseCase;

    @DeleteMapping(path = "/resumes/{slug}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticatedMember Handle handle,
            @PathVariable String slug,
            @PathVariable Long reviewId
    ) {
        manageReviewUseCase.delete(
                ManageReviewUseCase.DeleteReviewCommand.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .reviewId(reviewId)
                        .build()
        );

        return ResponseEntity.noContent().build();
    }
}
