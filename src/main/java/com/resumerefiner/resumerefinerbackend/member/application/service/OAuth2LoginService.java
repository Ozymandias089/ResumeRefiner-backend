package com.resumerefiner.resumerefinerbackend.member.application.service;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMemberPrincipal;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.OAuth2LoginUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2LoginService implements OAuth2LoginUseCase {
    private final MemberRepository memberRepository;

    /**
     * OAuth2 로그인 성공 후:
     * - 회원이 없으면 생성
     * - 있으면 조회
     * - 세션에 넣을 Principal 반환
     */
    @Override
    @Transactional
    public AuthenticatedMemberPrincipal loginOrRegister(OAuth2LoginCommand command) {

        // 1) provider + providerUserId로 먼저 찾기
        Member member = memberRepository
                .findByProviderAndProviderUserId(
                        command.provider(),
                        command.providerUserId()
                )
                .orElseGet(() -> findOrCreateByEmail(command));

        // 2) principal 생성
        return new AuthenticatedMemberPrincipal(
                member.getHandle(),
                member.getPasswordHash(), // 소셜이면 null
                member.getRole(),
                member.isActive()
        );
    }

    private Member findOrCreateByEmail(OAuth2LoginCommand command) {
        Email email = Email.of(command.email());

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DomainConflictException("EMAIL_ALREADY_EXISTS");
        }

        String handle = generateUniqueHandle(command.email(), command.provider());
        Member created = Member.registerSocial(
                handle,
                command.email(),
                normalizeName(command.name(), command.email()),
                command.provider(),
                command.providerUserId()
        );

        return memberRepository.save(created);
    }


    private String normalizeName(String name, String email) {
        if (name == null || name.isBlank()) return email;
        return name.trim();
    }

    /**
     * handle 자동 생성 규칙(최소):
     * - email local-part를 베이스로 sanitize
     * - 중복이면 숫자 suffix 증가
     */
    private String generateUniqueHandle(String email, Provider provider) {
        String base = email.split("@")[0]
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]", "");

        if (base.length() < 3) base = (provider.name().toLowerCase(Locale.ROOT) + "_" + base);
        if (base.length() < 3) base = "user";

        String candidate = base.length() > 32 ? base.substring(0, 32) : base;
        int i = 1;

        while (memberRepository.existsByHandle(Handle.of(candidate))) {
            String suffix = String.valueOf(++i);
            int maxBaseLen = 32 - suffix.length();
            String trimmedBase = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
            candidate = trimmedBase + suffix;
        }

        return candidate;
    }
}
