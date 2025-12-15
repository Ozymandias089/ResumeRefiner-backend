package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.member.application.dto.internal.LoginMember;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.ChangePasswordRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.ChangeProfileInfoRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ChangePasswordResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberProfileManagementController {
    private final GetProfileUseCase getProfileUseCase;
    private final ManageProfileUseCase manageProfileUseCase;

    @GetMapping(produces = "application/json")
    public ResponseEntity<MemberDetailsResponseDTO> getProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new IllegalStateException("UNAUTHORIZED");

        LoginMember login = (LoginMember) session.getAttribute("LOGIN_MEMBER");
        if (login == null) throw new IllegalStateException("UNAUTHORIZED");

        return ResponseEntity.ok(
                getProfileUseCase.getMyProfile(
                        new GetProfileUseCase.GetProfileCommand(
                                Handle.of(login.handle())
                        )
                )
        );
    }

    @PatchMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberDetailsResponseDTO> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody ChangeProfileInfoRequestDTO dto
    ) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new IllegalStateException("UNAUTHORIZED");

        LoginMember login = (LoginMember) session.getAttribute("LOGIN_MEMBER");
        if (login == null) throw new IllegalStateException("UNAUTHORIZED");

        return ResponseEntity.ok(
                manageProfileUseCase.changeUserInfo(
                        new ManageProfileUseCase.ChangeInfoCommand(
                                Handle.of(login.handle()),
                                Email.of(dto.email()),
                                dto.name()
                        )
                )
        );
    }

    @PatchMapping(path = "/password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequestDTO dto
    ) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new IllegalStateException("UNAUTHORIZED");

        LoginMember login = (LoginMember) session.getAttribute("LOGIN_MEMBER");
        if (login == null) throw new IllegalStateException("UNAUTHORIZED");

        return ResponseEntity.ok(
                manageProfileUseCase.changePassword(
                        new ManageProfileUseCase.ChangePasswordCommand(
                                Handle.of(login.handle()),
                                dto.oldPassword(),
                                dto.newPassword()
                        )
                )
        );
    }
}
