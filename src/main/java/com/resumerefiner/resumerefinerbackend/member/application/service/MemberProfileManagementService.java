package com.resumerefiner.resumerefinerbackend.member.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.UnauthorizedException;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImageRepository;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ChangePasswordResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberProfileManagementService implements GetProfileUseCase, ManageProfileUseCase {
    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;
    private final ResumeRepository resumeRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDetailsResponseDTO getMyProfile(GetProfileQuery query) {
        log.info("POST /api/profile Service Entry with Handle: {}", query.handle().toString());
        // 1. 커맨드에서 핸들을 통해 정보를 불러온다.
        Member member = memberRepository.findByHandle(query.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not found"));
        log.info("POST /api/profile Service \n Member {} found with id: {}", member.getHandle().toString(), member.getId());

        log.info("Getting member profile with id {}", member.getId());

        return toDTO(member);
    }

    @Override
    @Transactional
    public MemberDetailsResponseDTO changeUserInfo(ChangeInfoCommand command) {
        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not found"));

        if (command.newName() != null) {
            member.changeName(command.newName());
            log.info("Changing member name to '{}'", command.newName());
        }

        if (command.email() != null) {
            if (member.getProvider() != Provider.LOCAL) {
                log.error("Cannot change user's email address to '{}'", command.email());
                throw new UnauthorizedException("Social login Users cannot change Email");
            }
            member.changeEmail(command.email());
            log.info("Changing member email to '{}'", command.email());
        }

        memberRepository.save(member);
        return toDTO(member);
    }

    @Override
    @Transactional
    public ChangePasswordResponseDTO changePassword(ChangePasswordCommand command) {
        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not found"));

        if (member.getProvider() != Provider.LOCAL) {
            log.debug("Changing member profile password forbidden");
            throw new UnauthorizedException("social login user cannot change password");
        }

        // raw vs encoded
        if (!passwordEncoder.matches(command.currentPassword(), member.getPasswordHash())) {
            log.error("Passwords don't match");
            throw new InvalidCredentialsException("current password mismatch");
        }

        String newHash = passwordEncoder.encode(command.newPassword());
        member.changePassword(newHash); // 내부에서 provider LOCAL 체크는 있어도 ok
        log.info("Changing member profile password");

        // save는 선택: JPA면 dirty checking으로 없어도 됨
        memberRepository.save(member);

        return ChangePasswordResponseDTO.builder()
                .message("Password changed")
                .build();
    }

    private MemberDetailsResponseDTO toDTO(Member member) {
        String profileImageUrl = null;
        if (member.getProfileImageId() != null)
            profileImageUrl = memberProfileImageRepository.findUrlById(member.getProfileImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image Not Found"));

        log.info("SERVICE: Getting member profile image");

        // 3. 사용자의 id로 리뷰, 이력서 수 검색
        int resumeCount = resumeRepository.countByMemberId(member.getId());
        int reviewCount = reviewRepository.countByMemberId(member.getId());
        log.info("SERVICE: Counting Resumes({}) and reviews({})", resumeCount, reviewCount);

        // 4. 매핑 + 반환
        return MemberDetailsResponseDTO.builder()
                .id(member.getId())
                .handle(member.getHandle().toString())
                .email(member.getEmail().toString())
                .name(member.getName())
                .role(member.getRole().toString())
                .isActive(member.isActive())
                .provider(member.getProvider().toString())
                .providerUserId(member.getProviderUserId())
                .profileImageUrl(profileImageUrl)
                .credits(member.getCredits())
                .creditUpdatedAt(null)
                .resumeCount(resumeCount)
                .reviewCount(reviewCount)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
