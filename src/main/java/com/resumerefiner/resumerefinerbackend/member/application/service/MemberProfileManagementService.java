package com.resumerefiner.resumerefinerbackend.member.application.service;

import com.resumerefiner.resumerefinerbackend.media.domain.MediaFileRepository;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ChangePasswordResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
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
    private final MediaFileRepository mediaFileRepository;
    private final ResumeRepository resumeRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDetailsResponseDTO getMyProfile(GetProfileCommand command) {
        // 1. 커맨드에서 핸들을 통해 정보를 불러온다.
        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new RuntimeException("Member Not found"));
        log.info("Getting member profile with id {}", member.getId());

        // 2. 프로필 이미지 매핑
        String profileImageUrl = null;
        if (member.getProfileImageId() != null)
            profileImageUrl = mediaFileRepository.findById(member.getProfileImageId())
                    .orElseThrow(() -> new RuntimeException("Image Not Found"))
                    .getUrl();

        // 3. 사용자의 id로 리뷰, 이력서 수 검색
        int resumeCount = resumeRepository.countByMemberId(member.getId());
        int reviewCount = reviewRepository.countByMemberId(member.getId());
        log.debug("resume count {} with review count {}", resumeCount, reviewCount);

        // TODO: resolve creditUpdatedAt

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

    @Override
    @Transactional
    public MemberDetailsResponseDTO changeUserInfo(ChangeInfoCommand command) {
        // 1. 커맨드에서 핸들을 통해 정보를 불러온다. 필요한 정보는 이름, 이메일, 프로바이더이다.
        // 2. 프로바이더가 로컬이 아닌 경우 이메일을 수정할 수 없다.
        // 3. 커맨드 중 null이 아닌 것들만 적용한다.
        // 4. 저장한다.
        // 5. 반환한다.
        return null;
    }

    @Override
    @Transactional
    public ChangePasswordResponseDTO changePassword(ChangePasswordCommand command) {
        // 1. 커맨드에서 핸들을 통해 정보를 불러온다. 이때 정보는 핸들, 패스워드, 프로바이더이다.
        // 2. 프로바이더가 로컬이 아닌 경우 패스워드가 null일 것이다. 이때 에러를 던진다.
        // 2. 불러온 패스워드와 DTO로 받은 기존 패스워드가 일치하는지 판단한다.
        // 3. 새 패스워드를 적용한다.
        // 4. 저장한다.
        // 5. 반환한다.
        return null;
    }
}
