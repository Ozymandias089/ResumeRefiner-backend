package com.resumerefiner.resumerefinerbackend.resume.application.ports.out;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;

public interface ResumeSlugTitleRow {
    ResumeSlug getSlug();
    String getTitle();
}
