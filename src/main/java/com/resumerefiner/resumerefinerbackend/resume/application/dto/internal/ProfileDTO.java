package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import jakarta.validation.constraints.*;

public record ProfileDTO(
        @NotBlank
        @Size(max = 100)
        String name,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 30)
        @Pattern(regexp = "^[0-9+()\\-\\s]*$", message = "phone contains invalid characters")
        String phone,

        @Size(max = 255)
        String location
) {}

