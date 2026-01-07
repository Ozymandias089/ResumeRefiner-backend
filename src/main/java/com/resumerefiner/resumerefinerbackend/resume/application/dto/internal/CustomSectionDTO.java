package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSectionType;
import jakarta.validation.constraints.*;

public record CustomSectionDTO(
        @NotNull  ResumeSectionType type,
        @NotBlank @Size(max = 100) String subject,
        @NotBlank @Size(max = 8000) String content,
        @NotNull @Min(0) Integer displayOrder
) {}

