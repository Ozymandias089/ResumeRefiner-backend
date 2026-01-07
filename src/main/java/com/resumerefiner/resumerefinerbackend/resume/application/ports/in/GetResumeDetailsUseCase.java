package com.resumerefiner.resumerefinerbackend.resume.application.ports.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.Builder;

public interface GetResumeDetailsUseCase {

    GetResumeResponseDTO getResumeDetails(GetResumeDetailsQuery query);

    @Builder
    record GetResumeDetailsQuery(Handle handle, ResumeSlug slug){}
}
