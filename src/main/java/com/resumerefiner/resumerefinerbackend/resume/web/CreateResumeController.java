package com.resumerefiner.resumerefinerbackend.resume.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.request.CreateResumeRequestDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.CreateResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.infra.CreateResumeMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreateResumeController {
    private final CreateResumeUseCase createResumeUseCase;
    private final CreateResumeMapper createResumeMapper;

    @PostMapping(path = "/resumes", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Void> createResume(
            @AuthenticatedMember Handle handle,
            @Valid @RequestBody CreateResumeRequestDTO dto
            ){

        String slug = createResumeUseCase.create(createResumeMapper.toCommand(handle, dto));

        URI location = URI.create("/api/resumes" + slug);
        return ResponseEntity.created(location).build();
    }
}
