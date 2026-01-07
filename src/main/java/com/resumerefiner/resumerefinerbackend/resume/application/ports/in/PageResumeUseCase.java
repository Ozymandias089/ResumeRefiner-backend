package com.resumerefiner.resumerefinerbackend.resume.application.ports.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeSummaryListResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeSort;
import lombok.Builder;

public interface PageResumeUseCase {

    GetResumeSummaryListResponseDTO getResumeSummaryList(GetResumeSummaryQuery query);

    @Builder
    record GetResumeSummaryQuery(Handle handle, int page, int size, ResumeSort sort, String q){}
}
