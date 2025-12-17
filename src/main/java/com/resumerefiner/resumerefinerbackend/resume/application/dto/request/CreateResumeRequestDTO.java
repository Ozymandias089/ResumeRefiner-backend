package com.resumerefiner.resumerefinerbackend.resume.application.dto.request;

import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateResumeRequestDTO(
        @NotBlank @Size(max = 255) String title,

        @NotNull
        @Pattern(regexp = "^(KO|EN)$", message = "invalid languageCode")
        String languageCode, // 아래에서 enum 검증 추가 추천

        @Valid ProfileDTO profile,

        // 군필 정보는 "없을 수도" 있으면 NotNull 제거 권장(정책에 따라)
        @Valid MilitaryServiceDTO military,

        @NotNull @Size(min = 1) @Valid List<EducationDTO> education,

        @Valid List<ExperienceDTO> experiences,

        @Valid List<CustomSectionDTO> custom
) {}
