package com.resumerefiner.resumerefinerbackend.media.web;

import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadProfileImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.dto.internal.LoginMember;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            HttpServletRequest request,
            @RequestPart("file")MultipartFile file
            ){
        HttpSession session = request.getSession(false);
        if (session == null) throw new IllegalStateException("UNAUTHORIZED");

        LoginMember login = (LoginMember) session.getAttribute("LOGIN_MEMBER");
        if (login == null) throw new IllegalStateException("UNAUTHORIZED");

        return profileImageUseCase.uploadProfileImage(
                new ProfileImageUseCase.UploadProfileImageCommand(
                        Handle.of(login.handle()),
                        file
                )
        );
    }

}
