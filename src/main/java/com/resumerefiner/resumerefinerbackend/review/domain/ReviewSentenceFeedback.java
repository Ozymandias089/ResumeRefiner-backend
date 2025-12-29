package com.resumerefiner.resumerefinerbackend.review.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.DomainEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(
        name = "review_sentence_feedback",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_sentence_per_review",
                        columnNames = {"review_id", "sentence_index"}
                )
        },
        indexes = {
                @Index(name = "idx_sentence_review_id", columnList = "review_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSentenceFeedback extends BaseTimeEntity implements DomainEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 Review에 속한 문장 피드백인지
     */
    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    /**
     * 리뷰 내 문장 인덱스 (0-based 또는 1-based는 규칙 정해서 사용)
     */
    @Getter
    @Column(name = "sentence_index", nullable = false)
    private Integer sentenceIndex;

    @Getter
    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    /**
     * 이 문장에 대한 이슈 설명 텍스트 배열
     * - PostgreSQL text[]로 매핑
     */
    @Getter
    @ElementCollection
    @CollectionTable(
            name = "review_sentence_issue",
            joinColumns = @JoinColumn(name = "sentence_feedback_id")
    )
    @Column(name = "issue_text", nullable = false, columnDefinition = "TEXT")
    private List<String> issues;

    @Getter
    @Column(name = "suggestion", nullable = false, columnDefinition = "TEXT")
    private String suggestion;

    @Getter
    @Column(name = "clarity_score")
    private Short clarityScore;

    @Getter
    @Column(name = "tone_score")
    private Short toneScore;

    @Getter
    @Column(name = "logic_score")
    private Short logicScore;

    @Getter
    @Column(name = "rewritten_text", nullable = false, columnDefinition = "TEXT")
    private String rewrittenText;

    private ReviewSentenceFeedback(Review review,
                                   Integer sentenceIndex,
                                   String originalText,
                                   List<String> issues,
                                   String suggestion,
                                   Short clarityScore,
                                   Short toneScore,
                                   Short logicScore,
                                   String rewrittenText) {

        if (review == null) throw new DomainRuleViolationException("review must not be null");
        if (sentenceIndex == null || sentenceIndex < 0) {
            throw new DomainConflictException("sentenceIndex must be >= 0");
        }
        if (originalText == null) throw new DomainRuleViolationException("originalText must not be null");
        if (suggestion == null) throw new DomainRuleViolationException("suggestion must not be null");
        if (rewrittenText == null) throw new DomainRuleViolationException("rewrittenText must not be null");

        this.review = review;
        this.sentenceIndex = sentenceIndex;
        this.originalText = originalText;
        this.issues = issues;
        this.suggestion = suggestion;
        this.clarityScore = clarityScore;
        this.toneScore = toneScore;
        this.logicScore = logicScore;
        this.rewrittenText = rewrittenText;
    }

    // ==== 정적 팩토리 ====

    public static ReviewSentenceFeedback create(Review review,
                                                Integer sentenceIndex,
                                                String originalText,
                                                List<String> issues,
                                                String suggestion,
                                                Short clarityScore,
                                                Short toneScore,
                                                Short logicScore,
                                                String rewrittenText) {
        return new ReviewSentenceFeedback(
                review,
                sentenceIndex,
                originalText,
                issues,
                suggestion,
                clarityScore,
                toneScore,
                logicScore,
                rewrittenText
        );
    }

    // ==== 비즈니스 메서드 (필요 시) ====

    public void updateScores(Short clarity, Short tone, Short logic) {
        this.clarityScore = clarity;
        this.toneScore = tone;
        this.logicScore = logic;
    }

    public void updateIssues(List<String> issues) {
        this.issues = issues;
    }

    public void updateSuggestion(String suggestion, String rewrittenText) {
        if (suggestion == null) throw new DomainRuleViolationException("suggestion must not be null");
        if (rewrittenText == null) throw new DomainRuleViolationException("rewrittenText must not be null");
        this.suggestion = suggestion;
        this.rewrittenText = rewrittenText;
    }
}
