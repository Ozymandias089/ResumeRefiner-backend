package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import jakarta.validation.constraints.*;

public record ExperienceDTO(
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String role,
        @NotBlank @Size(max = 50) String period,
        @Size(max = 4000) String description,
        @NotNull @Min(0) Integer displayOrder
) {}
