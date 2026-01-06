package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMemberPrincipal;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InternalServerException;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.LoginRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberSummaryDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.RegisterMemberRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetMeUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.LoginUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.RegisterMemberUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterMemberUseCase registerMemberUseCase;
    private final LoginUseCase loginUseCase;
    private final AuthenticationManager authenticationManager;
    private final GetMeUseCase getMeUseCase;

    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberSummaryDTO> register(
            @Valid @RequestBody RegisterMemberRequestDTO requestDTO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        MemberSummaryDTO summary = registerMemberUseCase.register(
                new RegisterMemberUseCase.RegisterMemberCommand(
                        requestDTO.email(),
                        requestDTO.password(),
                        requestDTO.handle(),
                        requestDTO.name()
                )
        );

        authenticateAndLogin(requestDTO.email(), requestDTO.password(), request, response);

        return ResponseEntity.status(201).body(summary);
    }

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberSummaryDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Spring Security 세션 인증
        authenticateAndLogin(requestDto.email(), requestDto.password(), request, response);

        // 기존 DTO 반환 로직 유지 (원하면 principal 기반으로 바꿔도 됨)
        MemberSummaryDTO dto = loginUseCase.login(
                new LoginUseCase.LogInCommand(requestDto.email(), requestDto.password())
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public MemberSummaryDTO me(@AuthenticatedMember Handle handle) {
        return getMeUseCase.getMe(new GetMeUseCase.GetMeQuery(handle));
    }

    /**
     *  공통: 인증 수행 + SecurityContext 저장 + 세션 생성
     */
    // AuthController에 response도 받게
    private void authenticateAndLogin(
            String email,
            String password,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManager.authenticate(token);

        // ✅ context를 명시적으로 만들고 set
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // ✅ 세션 생성
        request.getSession(true);

        // ✅ 세션에 SecurityContext 저장 (Spring Session/Redis에서도 확실)
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.saveContext(context, request, response);

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedMemberPrincipal))
            throw new InternalServerException("Unexpected principal type: " + (principal != null ? principal.getClass() : null));
    }
}
