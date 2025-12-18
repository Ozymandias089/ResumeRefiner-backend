package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeExperience implements ValueObject {

    private static final int COMPANY_MAX = 255;
    private static final int ROLE_MAX = 255;
    private static final int PERIOD_MAX = 50;
    private static final int DESC_MAX = 4000;

    @Getter
    @Column(name = "company", nullable = false, length = COMPANY_MAX)
    private String company;

    @Getter
    @Column(name = "role", nullable = false, length = ROLE_MAX)
    private String role;

    @Getter
    @Column(name = "period", nullable = false, length = PERIOD_MAX)
    private String period; // "2022.01 ~ 2024.06" 같은 문자열로 시작 OK

    @Getter
    @Column(name = "description", length = DESC_MAX)
    private String description; // optional (업무/성과 bullet 텍스트 등)

    @Getter
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeExperience(
            String company,
            String role,
            String period,
            String description,
            int displayOrder
    ) {
        this.company = normalizeRequired(company, COMPANY_MAX, "EXP_COMPANY_REQUIRED");
        this.role = normalizeRequired(role, ROLE_MAX, "EXP_ROLE_REQUIRED");
        this.period = normalizeRequired(period, PERIOD_MAX, "EXP_PERIOD_REQUIRED");
        this.description = normalizeOptional(description, DESC_MAX);
        this.displayOrder = requireNonNegative(displayOrder, "EXP_ORDER_INVALID");
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static ResumeExperience of(
            String company,
            String role,
            String period,
            String description,
            int displayOrder
    ) {
        return ResumeExperience.builder()
                .company(company)
                .role(role)
                .period(period)
                .description(description)
                .displayOrder(displayOrder)
                .build();
    }

    public static ResumeExperience ofMinimal(
            String company,
            String role,
            String period,
            int displayOrder
    ) {
        return ResumeExperience.builder()
                .company(company)
                .role(role)
                .period(period)
                .displayOrder(displayOrder)
                .build();
    }

    /* ---------------------------
     * Domain actions (replace style)
     * --------------------------- */

    public ResumeExperience changeCompany(String company) {
        return new ResumeExperience(company, this.role, this.period, this.description, this.displayOrder);
    }

    public ResumeExperience changeRole(String role) {
        return new ResumeExperience(this.company, role, this.period, this.description, this.displayOrder);
    }

    public ResumeExperience changePeriod(String period) {
        return new ResumeExperience(this.company, this.role, period, this.description, this.displayOrder);
    }

    public ResumeExperience changeDescription(String description) {
        return new ResumeExperience(this.company, this.role, this.period, description, this.displayOrder);
    }

    public ResumeExperience clearDescription() {
        return new ResumeExperience(this.company, this.role, this.period, null, this.displayOrder);
    }

    public ResumeExperience reorder(int newDisplayOrder) {
        return new ResumeExperience(this.company, this.role, this.period, this.description, newDisplayOrder);
    }

    /** 공유/익명화: 회사명/역할/기간은 남기고 상세설명만 제거 */
    public ResumeExperience redactDetails() {
        return new ResumeExperience(this.company, this.role, this.period, null, this.displayOrder);
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
        if (v.length() > maxLen) throw new DomainRuleViolationException("VALUE_TOO_LONG");
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
        if (!(o instanceof ResumeExperience that)) return false;
        return displayOrder == that.displayOrder
                && Objects.equals(company, that.company)
                && Objects.equals(role, that.role)
                && Objects.equals(period, that.period)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, role, period, description, displayOrder);
    }
}
