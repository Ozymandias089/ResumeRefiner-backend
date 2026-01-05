package com.resumerefiner.resumerefinerbackend.review.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeExperience;
import lombok.Builder;

@Builder
public record ReviewExperienceSnapshot(
        String company,
        String role,
        String period,
        String description
) implements ValueObject {

    public static ReviewExperienceSnapshot from(ResumeExperience e) {
        if (e == null) throw new IllegalArgumentException("experience must not be null");
        return new ReviewExperienceSnapshot(
                nullSafe(e.getCompany()),
                nullSafe(e.getRole()),
                nullSafe(e.getPeriod()),
                nullSafe(e.getDescription())
        );
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

}
