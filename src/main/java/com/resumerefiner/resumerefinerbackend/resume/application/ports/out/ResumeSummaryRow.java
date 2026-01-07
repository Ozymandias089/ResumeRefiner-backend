package com.resumerefiner.resumerefinerbackend.resume.application.ports.out;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;

import java.time.Instant;

public interface ResumeSummaryRow {
    ResumeSlug getSlug();
    String getTitle();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
