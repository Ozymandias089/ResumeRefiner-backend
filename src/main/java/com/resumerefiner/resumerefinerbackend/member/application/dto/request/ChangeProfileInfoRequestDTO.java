package com.resumerefiner.resumerefinerbackend.member.application.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

public record ChangeProfileInfoRequestDTO(
        @Nullable String name,
        @Nullable @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") String email
) {}
