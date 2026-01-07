package com.resumerefiner.resumerefinerbackend.member.application.dto.response;

public record ValidateEmailResponseDTO(String email, boolean isAvailable) {
}
