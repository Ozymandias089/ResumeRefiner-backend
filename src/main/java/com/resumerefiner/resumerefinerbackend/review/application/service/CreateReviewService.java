package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.CreateReviewResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.CreateReviewUseCase;
import com.resumerefiner.resumerefinerbackend.review.application.port.out.ReviewLlmClient;
import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewCustomizationRequest;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewInputSnapshot;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewOutputSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateReviewService implements CreateReviewUseCase {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;
    private final ReviewLlmClient reviewLlmClient;

    @Override
    @Transactional
    public CreateReviewResponseDTO review(CreateReviewCommand command) {
        // ---- Validation Pipeline ---- //
        // 1. 멤버 id 로드
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));
        // 2. 이력서 로드
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        // 3. 로드한 멤버id와 이력서의 소유자 id가 일치하는지 조회
        if (!resume.getMemberId().equals(memberId)) {
            log.warn("Member id mismatch. memberId={}, resumeOwnerId={}", memberId, resume.getMemberId());
            throw new InvalidCredentialsException("Member id mismatch");
        }

        // 4) input snapshot json 생성
        ReviewInputSnapshot inputSnapshot = ReviewInputSnapshot.from(resume, command.stage());
        String inputSnapshotJson = toJson(inputSnapshot);
        int inputSchemaVersion = inputSnapshot.schemaVersion();

        // 5) customization request (optional)
        ReviewCustomizationRequest custom = null;
        if (command.customizationRequestJson() != null && !command.customizationRequestJson().isBlank()) {
            custom = ReviewCustomizationRequest.of(command.customizationRequestJson().trim(), 1);
        }

        ReviewLlmClient.Result llm = reviewLlmClient.generateReview(
                inputSnapshotJson,
                inputSchemaVersion,
                command.tone(),
                custom
        );

        String model = llm.model();
        Integer outputSchemaVersion = llm.outputSchemaVersion();
        String outputJson = llm.outputJson();

        ReviewOutputSnapshot output = ReviewOutputSnapshot.of(outputJson, outputSchemaVersion);

        // aggregate 생성
        Review review = Review.create(
                resume.getId(),
                resume.getVersion(),
                inputSnapshotJson,
                inputSchemaVersion,
                memberId,
                model,
                command.tone(),
                custom,
                output
        );

        // Seq + 1
        int nextSeq = reviewRepository.findMaxSequencePerVersion(resume.getId(), resume.getVersion()) + 1;
        review.assignSequencePerVersion(nextSeq);

        Review saved = reviewRepository.save(review);

        return CreateReviewResponseDTO.builder()
                .id(saved.getId())
                .resumeId(saved.getResumeId())
                .resumeVersion(saved.getResumeVersion())
                .sequencePerVersion(saved.getSequencePerVersion())
                .model(saved.getModel())
                .tone(saved.getTone())

                // input snapshot
                .inputSchemaVersion(saved.getSnapshotSchemaVersion())
                .inputSnapshotJson(saved.getReviewInputSnapshotJson())

                // custom request (nullable)
                .customRequestSchemaVersion(
                        saved.getCustomizationRequest() != null ? saved.getCustomizationRequest().getSchemaVersion() : null
                )
                .customRequestJson(
                        saved.getCustomizationRequest() != null ? saved.getCustomizationRequest().getJson() : null
                )

                // output (not null)
                .outputSchemaVersion(saved.getOutput().getSchemaVersion())
                .outputJson(saved.getOutput().getJson())

                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    private String toJson(Object v) {
        try {
            return objectMapper.writeValueAsString(v);
        } catch (JsonProcessingException e) {
            throw new DomainRuleViolationException("JSON serialization failed");
        }
    }
}
