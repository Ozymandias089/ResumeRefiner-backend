package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
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
public class ResumeEducation implements ValueObject {

    private static final int SCHOOL_MAX = 255;
    private static final int MAJOR_MAX = 255;
    private static final int PERIOD_MAX = 50;
    private static final int DESC_MAX = 2000;

    @Getter
    @Column(name = "school_name", nullable = false, length = SCHOOL_MAX)
    private String schoolName;

    @Getter
    @Column(name = "major", length = MAJOR_MAX)
    private String major; // optional

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "degree", length = 30)
    private EducationDegree degree; // optional

    @Getter
    @Column(name = "period", length = PERIOD_MAX)
    private String period; // optional (초기엔 "2019.03 ~ 2023.02" 같은 문자열 OK)

    @Getter
    @Column(name = "description", length = DESC_MAX)
    private String description; // optional

    @Getter
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeEducation(
            String schoolName,
            String major,
            EducationDegree degree,
            String period,
            String description,
            int displayOrder
    ) {
        this.schoolName = normalizeRequired(schoolName, SCHOOL_MAX, "EDU_SCHOOL_REQUIRED");
        this.major = normalizeOptional(major, MAJOR_MAX);
        this.degree = degree;
        this.period = normalizeOptional(period, PERIOD_MAX);
        this.description = normalizeMultiLineOptional(description, DESC_MAX);
        this.displayOrder = requireNonNegative(displayOrder, "EDU_ORDER_INVALID");
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static ResumeEducation of(
            String schoolName,
            String major,
            EducationDegree degree,
            String period,
            String description,
            int displayOrder
    ) {
        return ResumeEducation.builder()
                .schoolName(schoolName)
                .major(major)
                .degree(degree)
                .period(period)
                .description(description)
                .displayOrder(displayOrder)
                .build();
    }

    public static ResumeEducation ofMinimal(String schoolName, int displayOrder) {

        return ResumeEducation.builder()
                .schoolName(schoolName)
                .displayOrder(displayOrder)
                .build();
    }

    /* ---------------------------
     * Domain actions (replace style)
     * --------------------------- */

    public ResumeEducation changeSchoolName(String schoolName) {
        return new ResumeEducation(schoolName, this.major, this.degree, this.period, this.description, this.displayOrder);
    }

    public ResumeEducation changeMajor(String major) {
        return new ResumeEducation(this.schoolName, major, this.degree, this.period, this.description, this.displayOrder);
    }

    public ResumeEducation changeDegree(EducationDegree degree) {
        return new ResumeEducation(this.schoolName, this.major, degree, this.period, this.description, this.displayOrder);
    }

    public ResumeEducation changePeriod(String period) {
        return new ResumeEducation(this.schoolName, this.major, this.degree, period, this.description, this.displayOrder);
    }

    public ResumeEducation changeDescription(String description) {
        return new ResumeEducation(this.schoolName, this.major, this.degree, this.period, description, this.displayOrder);
    }

    public ResumeEducation reorder(int newDisplayOrder) {
        return new ResumeEducation(this.schoolName, this.major, this.degree, this.period, this.description, newDisplayOrder);
    }

    /* ---------------------------
     * Validation & normalization
     * --------------------------- */

    private static String normalizeRequired(String raw, int maxLen, String err) {
        String v = normalizeOptional(raw, maxLen);
        if (v == null || v.isBlank()) throw new DomainConflictException(err);
        return v;
    }

    private static String normalizeOptional(String raw, int maxLen) {
        if (raw == null) return null;
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) return null;
        if (v.length() > maxLen) throw new DomainConflictException("VALUE_TOO_LONG");
        return v;
    }

    private static String normalizeMultiLineOptional(String raw, int maxLen) {
        if (raw == null) return null;

        // 줄바꿈은 유지, 양 끝만 정리
        String v = raw
                .replaceAll("\r\n", "\n")  // 개행 통일
                .trim();

        if (v.isEmpty()) return null;
        if (v.length() > maxLen) {
            throw new DomainConflictException("VALUE_TOO_LONG");
        }
        return v;
    }

    private static int requireNonNegative(int value, String err) {
        if (value < 0) throw new DomainConflictException(err);
        return value;
    }

    /* ---------------------------
     * Value semantics
     * --------------------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResumeEducation that)) return false;
        return displayOrder == that.displayOrder
                && Objects.equals(schoolName, that.schoolName)
                && Objects.equals(major, that.major)
                && degree == that.degree
                && Objects.equals(period, that.period)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schoolName, major, degree, period, description, displayOrder);
    }
}
