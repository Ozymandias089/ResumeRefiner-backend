package com.resumerefiner.resumerefinerbackend.media.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.*;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase;
import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import com.resumerefiner.resumerefinerbackend.media.application.support.AbstractSingleImageUploadService;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImage;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImageRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileImageService extends AbstractSingleImageUploadService implements ProfileImageUseCase {

    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;

    public ProfileImageService(MediaStorage mediaStorage,
                               MemberRepository memberRepository,
                               MemberProfileImageRepository memberProfileImageRepository) {
        super(mediaStorage);
        this.memberRepository = memberRepository;
        this.memberProfileImageRepository = memberProfileImageRepository;
    }

    @Override
    @Transactional
    public UploadImageResponseDTO uploadProfileImage(UploadProfileImageCommand command) {
        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not Found"));

        AbstractSingleImageUploadService.Uploaded up = uploadToStorage("profile", member.getId(), command.file());

        memberProfileImageRepository.findByMemberId(member.getId()).ifPresent(existing -> {
            deleteFromStorageQuietly(existing.getStorageKey());
            memberProfileImageRepository.delete(existing);
            member.changeProfileImage(null);
        });

        MemberProfileImage saved = memberProfileImageRepository.save(
                MemberProfileImage.create(member.getId(), up.storageKey(), up.url(), up.originalName(), up.contentType(), up.sizeBytes())
        );

        member.changeProfileImage(saved.getId());
        memberRepository.save(member);

        return UploadImageResponseDTO.builder().url(saved.getUrl()).fileId(saved.getId()).build();
    }

    @Override
    @Transactional
    public void deleteProfileImage(DeleteProfileImageCommand command) {
        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not Found"));

        // FK 먼저 끊어서 정합성 확보(멱등)
        member.changeProfileImage(null);
        memberRepository.save(member);

        memberProfileImageRepository.findByMemberId(member.getId()).ifPresent(existing -> {
            deleteFromStorageQuietly(existing.getStorageKey());
            memberProfileImageRepository.delete(existing);
        });
    }

}
