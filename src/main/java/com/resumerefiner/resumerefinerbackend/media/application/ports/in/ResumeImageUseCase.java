package com.resumerefiner.resumerefinerbackend.media.application.ports.in;

import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeImageUseCase {
    UploadImageResponseDTO uploadResumeImage(UploadResumeImageCommand command);
    void deleteResumeImage(DeleteResumeImageCommand command);

    record UploadResumeImageCommand(Handle handle, ResumeSlug slug, MultipartFile file) {}
    record DeleteResumeImageCommand(Handle handle, ResumeSlug slug) {}
}
