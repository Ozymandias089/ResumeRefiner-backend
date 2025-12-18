package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.EducationDegree;
import jakarta.validation.constraints.*;

public record EducationDTO(
        @NotBlank @Size(max = 255) String schoolName,
        @Size(max = 255) String major,

        EducationDegree degree,

        @Size(max = 50) String period,
        @Size(max = 2000) String description,

        @NotNull @Min(0) Integer displayOrder
) {}
