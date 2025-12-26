package com.resumerefiner.resumerefinerbackend.resume.application.ports.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.request.UpdateResumeRequestDTO;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.Builder;

public interface UpdateResumeUseCase {
    long patch(UpdateResumeCommand command);

    @Builder
    record UpdateResumeCommand(
            Handle handle,
            ResumeSlug slug,
            long expectedVersion,
            UpdateResumeRequestDTO request
    ){}
}
