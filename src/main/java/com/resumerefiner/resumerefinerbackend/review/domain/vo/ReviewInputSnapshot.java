package com.resumerefiner.resumerefinerbackend.review.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.Gender;
import lombok.Builder;

import java.util.List;

@Builder
public record ReviewInputSnapshot(
        int schemaVersion,
        String languageCode,
        AgeRange ageRange,
        CareerStage careerStage,
        String gender,
        List<ReviewExperienceSnapshot> experiences,
        List<ReviewCustomSectionSnapshot> customSections
) implements ValueObject {
    public static final int SCHEMA_VERSION = 1;

    public static ReviewInputSnapshot from(Resume resume, CareerStage careerStageOverride) {
        if (resume == null) throw new IllegalArgumentException("resume must not be null");

        var lang = resume.getLanguageCode() != null ? resume.getLanguageCode().name() : "UNKNOWN";

        var birthDate = resume.getProfile() != null ? resume.getProfile().getBirthDate() : null;
        var ageRange = AgeRange.fromBirthDate(birthDate);

        var genderEnum = resume.getProfile() != null ? resume.getProfile().getGender() : null;
        var gender = mapGender(genderEnum);

        var exps = (resume.getExperiences() == null ? List.<ReviewExperienceSnapshot>of()
                : resume.getExperiences().stream().map(ReviewExperienceSnapshot::from).toList());

        var customs = (resume.getCustomSections() == null ? List.<ReviewCustomSectionSnapshot>of()
                : resume.getCustomSections().stream().map(ReviewCustomSectionSnapshot::from).toList());

        // careerStage는 “사용자 입력 우선” 권장. 없으면 UNKNOWN.
        var stage = careerStageOverride != null ? careerStageOverride : CareerStage.UNKNOWN;

        return new ReviewInputSnapshot(
                SCHEMA_VERSION,
                lang,
                ageRange,
                stage,
                gender,
                exps,
                customs
        );
    }

    private static String mapGender(Gender g) {
        if (g == null) return "UNKNOWN";
        return g.name(); // Resume Gender enum 그대로 문자열화
    }
}
