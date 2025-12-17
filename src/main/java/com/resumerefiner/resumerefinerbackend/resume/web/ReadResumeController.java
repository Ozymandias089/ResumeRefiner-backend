package com.resumerefiner.resumerefinerbackend.resume.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase.GetResumeDetailsCommand;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadResumeController {
    private final GetResumeDetailsUseCase getResumeDetailsUseCase;

    @GetMapping(path = "/resumes/{slug}")
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
}
