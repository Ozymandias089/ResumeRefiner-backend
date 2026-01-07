package com.resumerefiner.resumerefinerbackend.resume.application.dto.response;

import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.ResumeSummaryDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record GetResumeSummaryListResponseDTO(
        List<ResumeSummaryDTO> resumes,
        int page,
        int size,
        long totalElements,
        boolean hasPrev,
        boolean hasNext
) {}
