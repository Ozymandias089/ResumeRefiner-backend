package com.resumerefiner.resumerefinerbackend.resume.application.dto.response;

import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import com.resumerefiner.resumerefinerbackend.resume.domain.LanguageCode;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record GetResumeResponseDTO(
        String slug,
        String title,
        Instant createdAt,
        Instant updatedAt,
        LanguageCode languageCode,
        String photoUrl,
        ProfileViewDTO profile,
        MilitaryServiceDTO military,
        List<EducationDTO> educations,
        List<ExperienceDTO> experiences,
        List<CustomSectionDTO> customSections,
        long version
) {}
