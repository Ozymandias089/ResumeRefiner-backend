package com.resumerefiner.resumerefinerbackend.review.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeCustomSection;
import lombok.Builder;

@Builder
public record ReviewCustomSectionSnapshot(
        String type,
        String subject,
        String content
) implements ValueObject {

    public static ReviewCustomSectionSnapshot from(ResumeCustomSection s) {
        if (s == null) throw new IllegalArgumentException("customSection must not be null");
        return new ReviewCustomSectionSnapshot(
                s.getType() != null ? s.getType().name() : "UNKNOWN",
                nullSafe(s.getSubject()),
                nullSafe(s.getContent())
        );
    }

    private static String nullSafe(String v) {
        return v == null ? "" : v;
    }

}
