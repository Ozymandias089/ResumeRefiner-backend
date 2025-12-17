package com.resumerefiner.resumerefinerbackend.media.application.dto;

import lombok.Builder;

@Builder
public record UploadProfileImageResponseDTO(
        String profileImageUrl,
        Long fileId
) {}
