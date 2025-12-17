package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import jakarta.validation.constraints.*;

public record EducationDTO(
        @NotBlank @Size(max = 255) String schoolName,
        @Size(max = 255) String major,

        @Pattern(
                regexp = "^(HIGH_SCHOOL|ASSOCIATE|BACHELOR|MASTER|DOCTOR|OTHER)$",
                message = "invalid degree"
        )
        String degree,

        @Size(max = 50) String period,
        @Size(max = 2000) String description,

        @NotNull @Min(0) Integer displayOrder
) {}
