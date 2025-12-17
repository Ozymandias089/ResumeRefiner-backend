package com.resumerefiner.resumerefinerbackend.media.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadProfileImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase.UploadProfileImageCommand;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ProfileImageController {
    private final ProfileImageUseCase profileImageUseCase;

    @PostMapping(path = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadProfileImageResponseDTO uploadProfileImage(
            @AuthenticatedMember Handle handle,
            @RequestPart("file")MultipartFile file
            ){
        return profileImageUseCase.uploadProfileImage(
                new UploadProfileImageCommand(handle, file)
        );
    }

}
