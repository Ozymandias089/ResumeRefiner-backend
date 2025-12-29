package com.resumerefiner.resumerefinerbackend.review.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewCustomizationRequest;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewOutputSnapshot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_resume_id", columnList = "resume_id"),
                @Index(name = "idx_review_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity implements AggregateRoot {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 리뷰가 속한 Resume의 ID
     */
    @Getter
    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Getter
    @Column(name = "resume_version", nullable = false)
    private Long resumeVersion;

    @Getter
    @Column(name = "review_input_snapshot", nullable = false, columnDefinition = "jsonb")
    private String reviewInputSnapshotJson;

    @Getter
    @Column(name = "snapshot_schema_version", nullable = false)
    private Integer snapshotSchemaVersion;

    @Getter
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * 사용한 LLM 모델 이름 (예: gpt-5.1, claude-xxx 등)
     */
    @Getter
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    /**
     * 전체 톤 (FORMAL / NEUTRAL / PROFESSIONAL 등)
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "tone", nullable = false, length = 20)
    private ReviewTone tone;

    @Getter
    @Embedded
    private ReviewCustomizationRequest customizationRequest;

    @Getter
    @Embedded
    private ReviewOutputSnapshot output;

    // 양방향 필요하면 여기서 OneToMany 추가할 수도 있지만,
    // 일단은 ReviewSentenceFeedback 쪽에 ManyToOne만 두고 시작해도 됨.

    @Builder(access = AccessLevel.PRIVATE)
    private Review(Long resumeId,
                   Long resumeVersion,
                   String reviewInputSnapshotJson,
                   Integer snapshotSchemaVersion,
                   Long memberId,
                   String model,
                   ReviewTone tone,
                   ReviewCustomizationRequest customizationRequest,
                   ReviewOutputSnapshot output
    ) {

        if (resumeId == null) throw new DomainRuleViolationException("resumeId must not be null");
        if (resumeVersion == null || resumeVersion < 0) throw new DomainRuleViolationException("resumeVersion must be >= 0");
        if (reviewInputSnapshotJson == null || reviewInputSnapshotJson.isBlank())
            throw new DomainRuleViolationException("reviewInputSnapshotJson must not be blank");
        if (snapshotSchemaVersion == null || snapshotSchemaVersion <= 0)
            throw new DomainRuleViolationException("snapshotSchemaVersion must be > 0");
        if (memberId == null) throw new DomainRuleViolationException("memberId must not be null");
        if (model == null || model.isBlank()) throw new DomainRuleViolationException("model must not be blank");
        if (tone == null) throw new DomainRuleViolationException("tone must not be null");
        if (output == null) throw new DomainRuleViolationException("output must not be null");

        this.resumeId = resumeId;
        this.resumeVersion = resumeVersion;
        this.reviewInputSnapshotJson = reviewInputSnapshotJson;
        this.snapshotSchemaVersion = snapshotSchemaVersion;
        this.memberId = memberId;
        this.model = model;
        this.tone = tone;
        this.customizationRequest = customizationRequest;
        this.output = output;
    }

    // ==== 정적 팩토리 ====

    public static Review create(Long resumeId,
                                Long resumeVersion,
                                String reviewInputSnapshotJson,
                                Integer snapshotSchemaVersion,
                                Long memberId,
                                String model,
                                ReviewTone tone,
                                ReviewCustomizationRequest customizationRequest,
                                ReviewOutputSnapshot output
                                ) {
        return Review.builder()
                .resumeId(resumeId)
                .resumeVersion(resumeVersion)
                .reviewInputSnapshotJson(reviewInputSnapshotJson)
                .snapshotSchemaVersion(snapshotSchemaVersion)
                .memberId(memberId)
                .model(model)
                .tone(tone)
                .customizationRequest(customizationRequest)
                .output(output)
                .build();
    }

    public void updateOutput(ReviewOutputSnapshot output) {
        if (output == null) throw new DomainRuleViolationException("output must not be null");
        this.output = output;
    }
}
