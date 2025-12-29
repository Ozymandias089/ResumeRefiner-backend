package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.CreateReviewResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.CreateReviewUseCase;
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
        Integer inputSchemaVersion = inputSnapshot.schemaVersion();

        // 5) customization request (optional)
        ReviewCustomizationRequest custom = null;
        if (command.customizationRequestJson() != null && !command.customizationRequestJson().isBlank()) {
            custom = ReviewCustomizationRequest.of(command.customizationRequestJson().trim(), 1);
        }

        // 6) AI 요청 보내기 (MVP: 더미)
        // TODO: 실제 LLM 연동 서비스로 교체
        String model = "dummy-model";
        String outputJson = """
                {
                  "schemaVersion": 1,
                  "summary": "dummy summary",
                  "overallImprovedText": "dummy improved text",
                  "sectionResults": []
                }
                """;
        Integer outputSchemaVersion = 1;

        ReviewOutputSnapshot output = ReviewOutputSnapshot.of(outputJson, outputSchemaVersion);

        // 7) 도메인 객체 만들기 + 저장
        Review review = Review.create(
                resume.getId(),
                resume.getVersion(), // 너희 Resume에 맞게 getter 이름 확인 필요
                inputSnapshotJson,
                inputSchemaVersion,
                memberId,
                model,
                command.tone(),
                custom,
                output
        );

        Review saved = reviewRepository.save(review);

        return CreateReviewResponseDTO.builder()
                .id(saved.getId())
                .resumeId(saved.getResumeId())
                .resumeVersion(saved.getResumeVersion())
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
