package com.resumerefiner.resumerefinerbackend.global.security;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAuthProvider implements AuthenticationProvider {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        Member member = memberRepository.findByEmail(Email.of(email))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, member.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AuthenticatedMemberPrincipal principal =
                new AuthenticatedMemberPrincipal(
                        member.getHandle(),
                        member.getPasswordHash(),
                        member.getRole(),
                        member.isActive()
                );

        return new UsernamePasswordAuthenticationToken(
                principal,
                null, // 🔥 credentials 제거
                principal.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
