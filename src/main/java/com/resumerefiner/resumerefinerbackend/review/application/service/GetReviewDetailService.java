package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewDetailResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.GetReviewDetailUseCase;
import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetReviewDetailService implements GetReviewDetailUseCase {
    private final ReviewRepository reviewRepository;
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public GetReviewDetailResponseDTO get(GetReviewDetailQuery query) {
        Long memberId = memberRepository.findMemberIdByHandle(query.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));

        Review review = reviewRepository.findById(query.reviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!memberId.equals(review.getMemberId())) {
            log.error("Member id mismatch");
            throw new InvalidCredentialsException("Member id mismatch");
        }

        String slug = resumeRepository.findSlugById(review.getResumeId())
                .map(ResumeSlug::getValue)
                .orElse("UNKNOWN");

        String title = buildTitle(slug, review.getResumeVersion(), review.getSequencePerVersion());

        return GetReviewDetailResponseDTO.builder()
                .reviewId(review.getId())
                .title(title)
                .slug(slug)
                .resumeVersion(review.getResumeVersion())
                .sequencePerVersion(review.getSequencePerVersion())
                .tone(review.getTone())
                .model(review.getModel())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .inputSchemaVersion(review.getSnapshotSchemaVersion())
                .inputSnapshotJson(review.getReviewInputSnapshotJson())
                .customRequestSchemaVersion(
                        review.getCustomizationRequest() != null ? review.getCustomizationRequest().getSchemaVersion() : null
                )
                .customRequestJson(
                        review.getCustomizationRequest() != null ? review.getCustomizationRequest().getJson() : null
                )
                .outputSchemaVersion(review.getOutput().getSchemaVersion())
                .outputJson(review.getOutput().getJson())
                .build();
    }

    private String buildTitle(String slug, long resumeVersion, Integer seq) {
        int s = (seq == null ? 0 : seq);
        return "%s · v%d · #%d".formatted(slug, resumeVersion, s);
    }
}
