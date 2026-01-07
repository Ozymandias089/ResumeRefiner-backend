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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_resume_id", columnList = "resume_id"),
                @Index(name = "idx_review_member_id", columnList = "member_id"),
                @Index(name = "idx_review_resume_version", columnList = "resume_id,resume_version")
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

    /**
     * (resume_id, resume_version) 내에서의 순번 (#1, #2, ...)
     */
    @Getter
    @Column(name = "sequence_per_version", nullable = false)
    private Integer sequencePerVersion;

    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
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

    /**
     * 커스텀 요청 JSON (nullable)
     */
    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_request", columnDefinition = "jsonb")
    private String customRequestJson;

    @Getter
    @Column(name = "custom_request_schema_version")
    private Integer customRequestSchemaVersion;

    /**
     * LLM 결과 JSON (not null)
     */
    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "review_output", nullable = false, columnDefinition = "jsonb")
    private String reviewOutputJson;

    @Getter
    @Column(name = "output_schema_version", nullable = false)
    private Integer outputSchemaVersion;

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
        if (output.getJson() == null || output.getJson().isBlank())
            throw new DomainRuleViolationException("output json must not be blank");
        if (output.getSchemaVersion() <= 0)
            throw new DomainRuleViolationException("output schemaVersion must be > 0");

        this.resumeId = resumeId;
        this.resumeVersion = resumeVersion;
        this.reviewInputSnapshotJson = reviewInputSnapshotJson;
        this.snapshotSchemaVersion = snapshotSchemaVersion;
        this.memberId = memberId;
        this.model = model;
        this.tone = tone;

        // ✅ jsonb 1컬럼 저장(커스텀 요청은 nullable)
        this.customRequestJson = (customizationRequest != null ? customizationRequest.getJson() : null);
        this.customRequestSchemaVersion = (customizationRequest != null ? customizationRequest.getSchemaVersion() : null);

        // ✅ jsonb 1컬럼 저장(출력은 not null)
        this.reviewOutputJson = output.getJson();
        this.outputSchemaVersion = output.getSchemaVersion();

        // sequencePerVersion는 생성 시점에 서비스에서 assign한다.
        this.sequencePerVersion = 0; // 임시값 (DB NOT NULL이면 저장 전 반드시 assign되어야 함)
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

    /**
     * ✅ API/서비스 코드 호환용 (스펙 유지)
     * 기존에 review.getCustomizationRequest()로 읽던 코드가 그대로 동작하도록 복원 객체를 제공한다.
     */
    @Transient
    public ReviewCustomizationRequest getCustomizationRequest() {
        if (customRequestJson == null || customRequestJson.isBlank()) return null;
        int v = (customRequestSchemaVersion == null ? 1 : customRequestSchemaVersion);
        return ReviewCustomizationRequest.of(customRequestJson, v);
    }

    /**
     * ✅ API/서비스 코드 호환용 (스펙 유지)
     * 기존에 review.getOutput()으로 읽던 코드가 그대로 동작하도록 복원 객체를 제공한다.
     */
    @Transient
    public ReviewOutputSnapshot getOutput() {
        return ReviewOutputSnapshot.of(reviewOutputJson, outputSchemaVersion);
    }

    public String buildTitle(String resumeTitle) {
        int s = (this.sequencePerVersion == null ? 0 : this.sequencePerVersion);
        return "%s · v%d · #%d".formatted(resumeTitle, this.resumeVersion, s);
    }

    public void assignSequencePerVersion(int seq) {
        if (seq <= 0) throw new DomainRuleViolationException("sequencePerVersion must be > 0");
        this.sequencePerVersion = seq;
    }

    /**
     * 출력 갱신도 저장 필드로 직접 반영
     */
    public void updateOutput(ReviewOutputSnapshot output) {
        if (output == null) throw new DomainRuleViolationException("output must not be null");
        if (output.getJson() == null || output.getJson().isBlank())
            throw new DomainRuleViolationException("output json must not be blank");
        if (output.getSchemaVersion() <= 0)
            throw new DomainRuleViolationException("output schemaVersion must be > 0");

        this.reviewOutputJson = output.getJson();
        this.outputSchemaVersion = output.getSchemaVersion();
    }
}
