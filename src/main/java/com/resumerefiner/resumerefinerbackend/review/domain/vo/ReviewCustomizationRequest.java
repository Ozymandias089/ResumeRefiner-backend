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
public class ReviewCustomizationRequest implements ValueObject {

    @Getter
    @Column(name = "custom_request", columnDefinition = "jsonb")
    private String json; // 혹은 JsonNode / Map 으로 두고 Converter

    @Getter
    @Column(name = "custom_request_schema_version")
    private Integer schemaVersion;

    private ReviewCustomizationRequest(String json, Integer schemaVersion) {
        if (json == null || json.isBlank()) {
            // “요청 없음”이면 null로 두게 할 거면 여기서 막지 말고 factory에서 처리
            throw new DomainRuleViolationException("custom request json must not be blank");
        }
        this.json = json;
        this.schemaVersion = schemaVersion;
    }

    public static ReviewCustomizationRequest of(String json, Integer schemaVersion) {
        // normalize + length 제한 등을 여기서
        return new ReviewCustomizationRequest(json, schemaVersion);
    }

}
