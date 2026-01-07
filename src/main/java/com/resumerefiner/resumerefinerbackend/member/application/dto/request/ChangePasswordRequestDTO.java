package com.resumerefiner.resumerefinerbackend.member.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequestDTO(
        @NotNull String oldPassword,
        @NotNull String newPassword
) {}
