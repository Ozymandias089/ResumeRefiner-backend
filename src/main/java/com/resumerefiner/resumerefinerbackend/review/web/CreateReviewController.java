package com.resumerefiner.resumerefinerbackend.review.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.request.CreateReviewRequestDTO;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.CreateReviewResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.CreateReviewUseCase;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.CareerStage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreateReviewController {
    private final CreateReviewUseCase createReviewUseCase;

    @PostMapping(path = "/resumes/{slug}/reviews", consumes = "application/json")
    public ResponseEntity<CreateReviewResponseDTO> createReview(
            @PathVariable String slug,
            @AuthenticatedMember Handle handle,
            @RequestBody @Valid CreateReviewRequestDTO body
    ){
        CreateReviewResponseDTO response = createReviewUseCase.review(
                CreateReviewUseCase.CreateReviewCommand.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .tone(body.tone() != null ? body.tone() : ReviewTone.PROFESSIONAL)
                        .stage(body.careerStage() != null ? body.careerStage() : CareerStage.UNKNOWN)
                        .customizationRequestJson(body.customizationRequestJson())
                        .build()
        );

        return ResponseEntity.ok(response);
    }
}
