package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProfileDTO(
        @Size(max = 100)
        String name,

        @NotNull
        Gender gender,

        @Email
        @Size(max = 255)
        String email,

        @Size(max = 30)
        @Pattern(regexp = "^[0-9+()\\-\\s]*$", message = "phone contains invalid characters")
        String phone,

        @Size(max = 255)
        String location,

        @PastOrPresent(message = "birthDate must be in the past or present")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate
) {}

