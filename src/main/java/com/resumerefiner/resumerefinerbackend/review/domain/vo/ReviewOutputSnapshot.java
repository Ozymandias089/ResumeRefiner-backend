package com.resumerefiner.resumerefinerbackend.review.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewOutputSnapshot implements ValueObject {

    public static final int SCHEMA_VERSION = 1;
    private static final int JSON_MAX_LEN = 200_000; // 취향

    @Getter
    @Column(name = "review_output", nullable = false, columnDefinition = "jsonb")
    private String json;

    @Getter
    @Column(name = "output_schema_version", nullable = false)
    private Integer schemaVersion;

    private ReviewOutputSnapshot(String json, Integer schemaVersion) {
        var v = (json == null) ? null : json.trim();
        if (v == null || v.isBlank()) throw new DomainRuleViolationException("output json must not be blank");
        if (v.length() > JSON_MAX_LEN) throw new DomainRuleViolationException("output json too long");
        if (schemaVersion == null || schemaVersion <= 0)
            throw new DomainRuleViolationException("output schemaVersion must be > 0");

        this.json = v;
        this.schemaVersion = schemaVersion;
    }

    public static ReviewOutputSnapshot of(String json) {
        return new ReviewOutputSnapshot(json, SCHEMA_VERSION);
    }

    public static ReviewOutputSnapshot of(String json, Integer schemaVersion) {
        return new ReviewOutputSnapshot(json, schemaVersion);
    }
}
