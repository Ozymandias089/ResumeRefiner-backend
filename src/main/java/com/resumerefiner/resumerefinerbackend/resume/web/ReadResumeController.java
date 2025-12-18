package com.resumerefiner.resumerefinerbackend.resume.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeSummaryListResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase.GetResumeDetailsCommand;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.PageResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.PageResumeUseCase.GetResumeSummaryCommand;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeSort;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ReadResumeController {
    private final GetResumeDetailsUseCase getResumeDetailsUseCase;
    private final PageResumeUseCase pageResumeUseCase;

    @GetMapping(path = "/{slug}")
    public ResponseEntity<GetResumeResponseDTO> getResumeDetails(
            @AuthenticatedMember Handle handle,
            @PathVariable String slug
    ){
        GetResumeResponseDTO dto = getResumeDetailsUseCase.getResumeDetails(
                GetResumeDetailsCommand.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .build()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<GetResumeSummaryListResponseDTO> getResumeSummaries(
            @AuthenticatedMember Handle handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "UPDATED_AT_DESC") ResumeSort sort,
            @RequestParam(required = false) String q
    ) {
        GetResumeSummaryListResponseDTO dto = pageResumeUseCase.getResumeSummaryList(
                GetResumeSummaryCommand.builder()
                        .handle(handle)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .q(q)
                        .build()
        );

        return ResponseEntity.ok(dto);
    }
}
