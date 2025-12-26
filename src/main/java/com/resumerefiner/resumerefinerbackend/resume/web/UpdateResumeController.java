package com.resumerefiner.resumerefinerbackend.resume.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.PreconditionRequiredException;
import com.resumerefiner.resumerefinerbackend.global.web.http.Etags;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.request.UpdateResumeRequestDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.UpdateResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.UpdateResumeUseCase.UpdateResumeCommand;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class UpdateResumeController {
    private final UpdateResumeUseCase updateResumeUseCase;

    @PatchMapping(path = "/{slug}")
    public ResponseEntity<Void> updateResume(
            @AuthenticatedMember Handle handle,
            @PathVariable String slug,
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @Valid @RequestBody UpdateResumeRequestDTO updateResumeRequestDTO
    ) {
        Long expectedVersion = Etags.parseIfMatch(ifMatch);
        if (expectedVersion == null) {
            throw new PreconditionRequiredException("If-Match header is required");
        }

        long newVersion = updateResumeUseCase.patch(
                UpdateResumeCommand.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .expectedVersion(expectedVersion)
                        .request(updateResumeRequestDTO)
                        .build()
        );

        return ResponseEntity.noContent()
                .eTag(Etags.fromVersion(newVersion))
                .build();
    }
}
