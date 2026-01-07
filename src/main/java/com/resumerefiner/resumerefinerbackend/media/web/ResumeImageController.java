package com.resumerefiner.resumerefinerbackend.media.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ResumeImageUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ResumeImageController {
    private final ResumeImageUseCase resumeImageUseCase;

    @PostMapping(path = "/resume-image/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadImageResponseDTO uploadResumeImage(
            @AuthenticatedMember Handle handle,
            @PathVariable String slug,
            @RequestPart("file")MultipartFile file
            ) {
        return resumeImageUseCase.uploadResumeImage(
                new ResumeImageUseCase.UploadResumeImageCommand(
                        handle,
                        ResumeSlug.of(slug),
                        file
                )
        );
    }

    @DeleteMapping(path = "/resume-image/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResumeImage(
            @AuthenticatedMember Handle handle,
            @PathVariable String slug
    ){
        resumeImageUseCase.deleteResumeImage(
                new ResumeImageUseCase.DeleteResumeImageCommand(handle, ResumeSlug.of(slug))
        );
    }
}
