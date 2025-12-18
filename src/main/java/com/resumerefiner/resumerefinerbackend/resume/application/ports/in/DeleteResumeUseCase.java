package com.resumerefiner.resumerefinerbackend.resume.application.ports.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.Builder;

public interface DeleteResumeUseCase {
    void delete(DeleteResumeCommand command);

    @Builder record DeleteResumeCommand(Handle handle, ResumeSlug slug){}
}
