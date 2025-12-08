package com.resumerefiner.resumerefinerbackend.review.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_resume_id", columnList = "resume_id")
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
     * 전체 요약 피드백
     */
    @Getter
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    /**
     * 개선된 이력서 전체 텍스트 (LLM이 다듬은 버전)
     */
    @Getter
    @Column(name = "improved_text", nullable = false, columnDefinition = "TEXT")
    private String improvedText;

    @Getter
    @Column(name = "score_structure")
    private Short scoreStructure;

    @Getter
    @Column(name = "score_clarity")
    private Short scoreClarity;

    @Getter
    @Column(name = "score_tone")
    private Short scoreTone;

    // 양방향 필요하면 여기서 OneToMany 추가할 수도 있지만,
    // 일단은 ReviewSentenceFeedback 쪽에 ManyToOne만 두고 시작해도 됨.

    private Review(Long resumeId,
                   String model,
                   ReviewTone tone,
                   String summary,
                   String improvedText,
                   Short scoreStructure,
                   Short scoreClarity,
                   Short scoreTone) {

        if (resumeId == null) throw new IllegalArgumentException("resumeId must not be null");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("model must not be blank");
        if (tone == null) throw new IllegalArgumentException("tone must not be null");
        if (summary == null) throw new IllegalArgumentException("summary must not be null");
        if (improvedText == null) throw new IllegalArgumentException("improvedText must not be null");

        this.resumeId = resumeId;
        this.model = model;
        this.tone = tone;
        this.summary = summary;
        this.improvedText = improvedText;
        this.scoreStructure = scoreStructure;
        this.scoreClarity = scoreClarity;
        this.scoreTone = scoreTone;
    }

    // ==== 정적 팩토리 ====

    public static Review create(Long resumeId,
                                String model,
                                ReviewTone tone,
                                String summary,
                                String improvedText,
                                Short scoreStructure,
                                Short scoreClarity,
                                Short scoreTone) {
        return new Review(
                resumeId,
                model,
                tone,
                summary,
                improvedText,
                scoreStructure,
                scoreClarity,
                scoreTone
        );
    }

    // ==== 비즈니스 메서드 ====

    public void updateScores(Short structure, Short clarity, Short toneScore) {
        this.scoreStructure = structure;
        this.scoreClarity = clarity;
        this.scoreTone = toneScore;
    }

    public void updateSummary(String newSummary) {
        if (newSummary == null) throw new IllegalArgumentException("summary must not be null");
        this.summary = newSummary;
    }

    public void updateImprovedText(String newImprovedText) {
        if (newImprovedText == null) throw new IllegalArgumentException("improvedText must not be null");
        this.improvedText = newImprovedText;
    }
}
