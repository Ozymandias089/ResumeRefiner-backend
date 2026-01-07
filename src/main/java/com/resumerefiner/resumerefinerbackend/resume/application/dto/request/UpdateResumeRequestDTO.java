package com.resumerefiner.resumerefinerbackend.resume.application.dto.request;

import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import com.resumerefiner.resumerefinerbackend.resume.domain.LanguageCode;

import java.util.List;

public record UpdateResumeRequestDTO(
        String title,
        LanguageCode languageCode,
        ProfilePatchDTO profile,
        MilitaryServicePatchDTO militaryService,
        List<EducationDTO> educations,
        List<ExperienceDTO> experiences,
        List<CustomSectionDTO> customSections,
        boolean clearMilitaryService
) {}
