package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeCustomSection implements ValueObject {

    private static final int SUBJECT_MAX = 100;
    private static final int CONTENT_MAX = 8000;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", length = 30)
    private ResumeSectionType type; // optional (null 허용)

    @Getter
    @Column(name = "subject", nullable = false, length = SUBJECT_MAX)
    private String subject;

    @Getter
    @Column(name = "content", nullable = false, length = CONTENT_MAX)
    private String content;

    @Getter
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeCustomSection(
            ResumeSectionType type,
            String subject,
            String content,
            int displayOrder
    ) {
        this.type = type;
        this.subject = normalizeRequired(subject, SUBJECT_MAX, "SECTION_SUBJECT_REQUIRED");
        this.content = normalizeRequired(content, CONTENT_MAX, "SECTION_CONTENT_REQUIRED");
        this.displayOrder = requireNonNegative(displayOrder, "SECTION_ORDER_INVALID");
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static ResumeCustomSection of(
            ResumeSectionType type,
            String subject,
            String content,
            int displayOrder
    ) {
        return ResumeCustomSection.builder()
                .type(type)
                .subject(subject)
                .content(content)
                .displayOrder(displayOrder)
                .build();
    }

    public static ResumeCustomSection ofMinimal(
            String subject,
            String content,
            int displayOrder
    ) {
        return ResumeCustomSection.builder()
                .subject(subject)
                .content(content)
                .displayOrder(displayOrder)
                .build();
    }

    /* ---------------------------
     * Domain actions (replace style)
     * --------------------------- */

    public ResumeCustomSection changeType(ResumeSectionType type) {
        return new ResumeCustomSection(type, this.subject, this.content, this.displayOrder);
    }

    public ResumeCustomSection changeSubject(String subject) {
        return new ResumeCustomSection(this.type, subject, this.content, this.displayOrder);
    }

    public ResumeCustomSection changeContent(String content) {
        return new ResumeCustomSection(this.type, this.subject, content, this.displayOrder);
    }

    public ResumeCustomSection reorder(int newDisplayOrder) {
        return new ResumeCustomSection(this.type, this.subject, this.content, newDisplayOrder);
    }

    /** 공유/익명화: 제목은 유지, 내용은 제거 */
    public ResumeCustomSection redactContent() {
        return new ResumeCustomSection(this.type, this.subject, "", this.displayOrder);
    }

    /* ---------------------------
     * Validation & normalization
     * --------------------------- */

    private static String normalizeRequired(String raw, int maxLen, String err) {
        String v = normalizeOptional(raw, maxLen);
        if (v == null || v.isBlank()) throw new IllegalArgumentException(err);
        return v;
    }

    private static String normalizeOptional(String raw, int maxLen) {
        if (raw == null) return null;
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) return null;
        if (v.length() > maxLen) throw new IllegalArgumentException("VALUE_TOO_LONG");
        return v;
    }

    private static int requireNonNegative(int value, String err) {
        if (value < 0) throw new IllegalArgumentException(err);
        return value;
    }

    /* ---------------------------
     * Value semantics
     * --------------------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResumeCustomSection that)) return false;
        return displayOrder == that.displayOrder
                && type == that.type
                && Objects.equals(subject, that.subject)
                && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subject, content, displayOrder);
    }
}
