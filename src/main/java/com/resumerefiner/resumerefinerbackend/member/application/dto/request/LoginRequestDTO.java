package com.resumerefiner.resumerefinerbackend.member.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull String email,
        @NotNull String password
) {
}
