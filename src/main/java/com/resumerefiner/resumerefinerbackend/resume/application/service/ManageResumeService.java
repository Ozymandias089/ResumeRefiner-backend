package com.resumerefiner.resumerefinerbackend.resume.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ForbiddenException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.media.domain.MediaFileRepository;
import com.resumerefiner.resumerefinerbackend.media.domain.MediaOwnerType;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.DeleteResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageResumeService implements DeleteResumeUseCase {
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;
    private final MediaFileRepository mediaFileRepository;

    @Override
    @Transactional
    public void delete(DeleteResumeCommand command) {
        // 핸들로 멤버 ID 로드
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));
        log.debug("Loaded Member id {} with handle {}", memberId, command.handle());

        // 슬러그로 이력서 로드
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + command.slug()));
        log.debug("Loaded Resume id {} with slug {}", resume.getId(), command.slug().toString());

        // 멤버 ID와 이력서의 멤버ID가 일치하는지 검증
        if (!memberId.equals(resume.getMemberId())){
            log.error("Member id mismatch");
            throw new ForbiddenException();
        }

        // 이력서의 이미지 파일 존재 시 삭제
        Long photoId = resume.getPhotoImageId();
        if (photoId != null) {
            mediaFileRepository.findById(photoId).ifPresent(f -> {
                if (f.getOwnerType() == MediaOwnerType.RESUME && f.getOwnerId().equals(resume.getId())) {
                    mediaFileRepository.deleteByOwnerTypeAndOwnerId(MediaOwnerType.RESUME, resume.getId());
                } else {
                    log.warn("PhotoImageId points to unexpected owner. mediaId={}, ownerType={}, ownerId={}, resumeId={}",
                            photoId, f.getOwnerType(), f.getOwnerId(), resume.getId());
                }
            });
        }

        // 삭제
        resumeRepository.delete(resume);
        log.info("Deleted Resume id {} with slug {}", resume.getId(), command.slug().toString());
    }
}
