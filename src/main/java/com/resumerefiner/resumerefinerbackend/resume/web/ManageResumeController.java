package com.resumerefiner.resumerefinerbackend.resume.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.DeleteResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.DeleteResumeUseCase.DeleteResumeCommand;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ManageResumeController {
    private final DeleteResumeUseCase deleteResumeUseCase;

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> delete(@AuthenticatedMember Handle handle, @PathVariable String slug) {
        deleteResumeUseCase.delete(
                DeleteResumeCommand.builder()
                        .handle(handle)
                        .slug(ResumeSlug.of(slug))
                        .build()
        );
        return ResponseEntity.noContent().build();
    }
}
