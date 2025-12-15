package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.member.application.dto.internal.LoginMember;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberProfileManagementController {
    private final GetProfileUseCase getProfileUseCase;

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
}
