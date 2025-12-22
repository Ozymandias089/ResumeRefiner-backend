package com.resumerefiner.resumerefinerbackend.member.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.*;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImageRepository;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberSummaryDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetMeUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.LoginUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.RegisterMemberUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAuthService implements RegisterMemberUseCase, LoginUseCase, GetMeUseCase {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberSummaryDTO getMe(GetMeCommand q) {
        Member member = memberRepository.findByHandle(new Handle(q.handle().getValue()))
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return toMemberSummary(member);
    }

    @Override
    public MemberSummaryDTO login(LogInCommand command) {
        Member member = memberRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new InvalidCredentialsException("Account not found"));

        if (!passwordEncoder.matches(command.password(), member.getPasswordHash())) {
            log.error("Invalid credentials");
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!member.isActive()) {
            throw new MemberInactiveException("Member not active");
        }

        return toMemberSummary(member);
    }

    @Override
    @Transactional
    public MemberSummaryDTO register(RegisterMemberCommand command) {
        if (memberRepository.existsByEmail(new Email(command.email()))) {
            log.error("Email already in use");
            throw new DuplicateEmailException();
        }
        if (memberRepository.existsByHandle(new Handle(command.handle()))) {
            log.error("Email already in use");
            throw new DuplicateHandleException();
        }

        // 2) 비밀번호 해시
        String encodedPassword = passwordEncoder.encode(command.password());

        // 3) 도메인 엔티티 생성
        Member member = Member.registerLocal(
                command.handle(),
                command.email(),
                encodedPassword,
                command.name()
        );

        Member savedMember = memberRepository.save(member);
        log.info("Member saved with id={}, handle={}", savedMember.getId(), savedMember.getHandle());
        return toMemberSummary(savedMember);
    }

    private MemberSummaryDTO toMemberSummary(Member m) {

        int resumeCount = resumeRepository.countByMemberId(m.getId());
        int reviewCount = reviewRepository.countByMemberId(m.getId());

        // 2. 프로필 이미지 매핑
        String profileImageUrl = null;
        if (m.getProfileImageId() != null)
            profileImageUrl = memberProfileImageRepository.findUrlById(m.getProfileImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image Not Found"));

        return new MemberSummaryDTO(
                m.getId(),
                m.getHandle().getValue(),
                m.getEmail().getValue(),
                m.getName(),
                m.getRole().name(),
                m.isActive(),
                profileImageUrl,

                m.getCredits(),
                null,

                resumeCount,
                reviewCount,

                m.getCreatedAt() != null ? m.getCreatedAt() : Instant.now()
        );
    }
}
