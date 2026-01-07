package com.resumerefiner.resumerefinerbackend.global.security.oauth;

import com.resumerefiner.resumerefinerbackend.config.WebAppProperties;
import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMemberPrincipal;
import com.resumerefiner.resumerefinerbackend.global.security.SessionLoginSupport;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.OAuth2LoginUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2LoginUseCase oAuth2LoginUseCase;
    private final SessionLoginSupport sessionLoginSupport;
    private final WebAppProperties webAppProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OidcUser oidcUser = (OidcUser) token.getPrincipal();

        // google만 쓴다고 가정 (registrationId로 매핑해도 됨)
        Provider provider = Provider.GOOGLE;

        String providerUserId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        AuthenticatedMemberPrincipal principal = oAuth2LoginUseCase.loginOrRegister(
                new OAuth2LoginUseCase.OAuth2LoginCommand(
                        provider,
                        providerUserId,
                        email,
                        name
                )
        );

        sessionLoginSupport.login(principal, request, response);

        response.sendRedirect(webAppProperties.oauthSuccessUrl());
    }
}
