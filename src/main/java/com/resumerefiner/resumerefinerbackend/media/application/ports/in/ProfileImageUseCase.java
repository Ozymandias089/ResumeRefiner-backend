package com.resumerefiner.resumerefinerbackend.media.application.ports.in;

import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadProfileImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageUseCase {

    UploadProfileImageResponseDTO uploadProfileImage(UploadProfileImageCommand command);

    record UploadProfileImageCommand(
            Handle handle,
            MultipartFile file
    ){}
}
