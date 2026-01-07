package com.resumerefiner.resumerefinerbackend.media.application.ports.in;

import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUseCase {

    UploadImageResponseDTO uploadProfileImage(UploadProfileImageCommand command);

    void deleteProfileImage(DeleteProfileImageCommand command);

    record UploadProfileImageCommand(
            Handle handle,
            MultipartFile file
    ){}

    record DeleteProfileImageCommand(Handle handle){}
}
