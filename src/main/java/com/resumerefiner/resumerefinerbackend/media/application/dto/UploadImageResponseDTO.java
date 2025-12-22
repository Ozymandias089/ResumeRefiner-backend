package com.resumerefiner.resumerefinerbackend.media.application.dto;

import lombok.Builder;

@Builder
public record UploadImageResponseDTO(String url, Long fileId) {}
