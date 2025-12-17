package com.resumerefiner.resumerefinerbackend.member.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record RegisterMemberRequestDTO(
        @NotNull String email,
        @NotNull String password,
        @NotNull String handle,
        @NotNull String name
) {}
