package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.global.util.SessionUtil;
import com.resumerefiner.resumerefinerbackend.member.application.dto.internal.LoginMember;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.LoginRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberSummaryDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.RegisterMemberRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetMeUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.LoginUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.RegisterMemberUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterMemberUseCase registerMemberUseCase;
    private final LoginUseCase loginUseCase;
    private final GetMeUseCase getMeUseCase;

    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberSummaryDTO> register(
            @Valid @RequestBody RegisterMemberRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        MemberSummaryDTO summary = registerMemberUseCase.register(
                new RegisterMemberUseCase.RegisterMemberCommand(
                        requestDTO.email(),
                        requestDTO.password(),
                        requestDTO.handle(),
                        requestDTO.name()
                )
        );

        LoginMember loginMember = new LoginMember(
                summary.id(),
                summary.handle(),
                summary.role(),
                summary.isActive(),
                summary.credits()
        );

        SessionUtil.setLoginMember(request, loginMember);

        // 201 Created + body
        return ResponseEntity.status(201).body(summary);
    }

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberSummaryDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO,
            HttpServletRequest request
    ) {
        MemberSummaryDTO summary = loginUseCase.login(new LoginUseCase.LogInCommand(requestDTO.email(), requestDTO.password()));
        LoginMember loginMember = new LoginMember(summary.id(), summary.handle(), summary.role(), summary.isActive(), summary.credits());

        SessionUtil.setLoginMember(request, loginMember);

        return ResponseEntity.ok(summary); // 200 OK
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SessionUtil.invalidate(request);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/me")
    public MemberSummaryDTO me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        LoginMember login = (LoginMember) session.getAttribute("LOGIN_MEMBER");
        if (login == null) {
            throw new IllegalStateException("UNAUTHORIZED");
        }

        return getMeUseCase.getMe(new GetMeUseCase.GetMeCommand(new Handle(login.handle())));
    }
}
