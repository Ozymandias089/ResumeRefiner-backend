package com.resumerefiner.resumerefinerbackend.media.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase.UploadProfileImageCommand;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase.DeleteProfileImageCommand;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ProfileImageController {
    private final ProfileImageUseCase profileImageUseCase;

    @PostMapping(path = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadImageResponseDTO uploadProfileImage(
            @AuthenticatedMember Handle handle,
            @RequestPart("file")MultipartFile file
            ){
        return profileImageUseCase.uploadProfileImage(
                new UploadProfileImageCommand(handle, file)
        );
    }

    @DeleteMapping(path = "/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileImage(@AuthenticatedMember Handle handle) {
        profileImageUseCase.deleteProfileImage(
                new DeleteProfileImageCommand(handle)
        );
    }
}
