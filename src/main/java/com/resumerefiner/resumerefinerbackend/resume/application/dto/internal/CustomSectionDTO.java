package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import jakarta.validation.constraints.*;

public record CustomSectionDTO(
        @Pattern(
                regexp = "^(INTRODUCTION|PROJECT|CERTIFICATION|AWARD|ACTIVITY|MILITARY_NOTE|OTHER)$",
                message = "invalid type"
        )
        String type,

        @NotBlank @Size(max = 100) String subject,
        @NotBlank @Size(max = 8000) String content,
        @NotNull @Min(0) Integer displayOrder
) {}

