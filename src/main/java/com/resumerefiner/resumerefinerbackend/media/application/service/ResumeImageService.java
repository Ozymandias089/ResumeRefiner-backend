package com.resumerefiner.resumerefinerbackend.media.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ForbiddenException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ResumeImageUseCase;
import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import com.resumerefiner.resumerefinerbackend.media.application.support.AbstractSingleImageUploadService;
import com.resumerefiner.resumerefinerbackend.media.domain.resume.ResumeImage;
import com.resumerefiner.resumerefinerbackend.media.domain.resume.ResumeImageRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumeImageService extends AbstractSingleImageUploadService implements ResumeImageUseCase {
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeImageRepository resumeImageRepository;

    public ResumeImageService(
            MediaStorage mediaStorage,
            MemberRepository memberRepository,
            ResumeRepository resumeRepository,
            ResumeImageRepository resumeImageRepository
    ) {
        super(mediaStorage);
        this.memberRepository = memberRepository;
        this.resumeRepository = resumeRepository;
        this.resumeImageRepository = resumeImageRepository;
    }

    @Override
    @Transactional
    public UploadImageResponseDTO uploadResumeImage(UploadResumeImageCommand command) {
        // 1. 커맨드의 핸들을 통해 멤버 id를 가져온다.
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));
        // 2. 커맨드의 Slug를 통해 이력서를 가져온다.
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        // 3. 이력서의 memberId와 핸들을 통해 가져온 id가 매치하는지 판단한다.
        if (!resume.getMemberId().equals(memberId)) throw new ForbiddenException();

        Uploaded up = uploadToStorage("resume", resume.getId(), command.file());

        // 기존 이미지 정리(대표 1장)
        resumeImageRepository.findByResumeId(resume.getId()).ifPresent(existing -> {
            deleteFromStorageQuietly(existing.getStorageKey());
            resumeImageRepository.delete(existing);
        });

        ResumeImage saved = resumeImageRepository.save(
                ResumeImage.create(
                        resume.getId(),
                        up.storageKey(),
                        up.url(),
                        up.originalName(),
                        up.contentType(),
                        up.sizeBytes()
                )
        );

        // 최종적으로 새 id로 덮어쓰기
        resume.changePhotoImageId(saved.getId());
        resumeRepository.save(resume);

        return UploadImageResponseDTO.builder()
                .url(saved.getUrl())
                .fileId(saved.getId())
                .build();
    }

    @Override
    @Transactional
    public void deleteResumeImage(DeleteResumeImageCommand command) {
        // 1. 커맨드의 핸들을 통해 memberId를 가져온다.
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not Found"));

        // 2. 커맨드의 slug를 통해 이력서를 가져온다.
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // 3. 이력서의 memberId와 세션 사용자의 memberId를 비교한다.
        if (!resume.getMemberId().equals(memberId)) throw new ForbiddenException();

        // 우선 FK를 끊고 저장 (이미지 레코드가 없더라도 일관성 유지)
        resume.changePhotoImageId(null);
        resumeRepository.save(resume);

        // 이미지가 있으면 스토리지+DB 정리
        resumeImageRepository.findByResumeId(resume.getId()).ifPresent(existing -> {
            deleteFromStorageQuietly(existing.getStorageKey());
            resumeImageRepository.delete(existing);
        });
    }
}
