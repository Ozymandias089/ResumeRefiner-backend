package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMemberPrincipal;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;

public interface OAuth2LoginUseCase {
    record OAuth2LoginCommand(
            Provider provider,   // "google"
            String providerUserId, // sub
            String email,
            String name
    ) {}

    /**
     * OAuth2 로그인 성공 후:
     * - 회원이 없으면 생성
     * - 있으면 조회
     * - 세션에 넣을 Principal 반환
     */
    AuthenticatedMemberPrincipal loginOrRegister(OAuth2LoginCommand command);
}
