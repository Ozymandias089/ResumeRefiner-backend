package com.resumerefiner.resumerefinerbackend.media.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.*;
import com.resumerefiner.resumerefinerbackend.media.application.dto.UploadProfileImageResponseDTO;
import com.resumerefiner.resumerefinerbackend.media.application.ports.in.ProfileImageUseCase;
import com.resumerefiner.resumerefinerbackend.media.application.ports.out.MediaStorage;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImage;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImageRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileImageService implements ProfileImageUseCase {
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CT = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private final MemberRepository memberRepository;       // 포트든 JPA든 너 구조대로
    private final MemberProfileImageRepository memberProfileImageRepository; // media_file 저장
    private final MediaStorage mediaStorage;               // S3 어댑터
    // (선택) 기존 이미지 삭제까지 하고 싶으면 MediaFile 조회/삭제 + S3 delete도 추가

    @Override
    @Transactional
    public UploadProfileImageResponseDTO uploadProfileImage(UploadProfileImageCommand command) {

        Member member = memberRepository.findByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member Not Found"));

        MultipartFile file = command.file();
        validateImage(file);

        String contentType = normalizeContentType(file.getContentType());
        String ext = contentTypeToExt(contentType);

        String key = "profile/%d/%s.%s".formatted(member.getId(), UUID.randomUUID(), ext);

        final String url;
        try (InputStream in = file.getInputStream()) {
            url = mediaStorage.uploadPublic(key, contentType, in, file.getSize());
        } catch (IOException e) {
            throw new InternalServerException("Failed to upload file : " + e.getMessage());
        }

        // 기존 이미지 레코드 제거(대표 1장 정책)
        memberProfileImageRepository.findByMemberId(member.getId()).ifPresent(existing -> {
            memberProfileImageRepository.delete(existing);
            // member.profileImageId는 아래에서 새걸로 덮어쓸 거라 null 세팅은 필수는 아님.
            // 다만 "중간 상태"를 명시하고 싶으면 유지해도 OK.
            member.changeProfileImage(null);
        });

        String originalName = (file.getOriginalFilename() == null) ? "profile." + ext : file.getOriginalFilename();
        MemberProfileImage toSave = MemberProfileImage.create(
                member.getId(),
                url,
                originalName,
                contentType,
                file.getSize()
        );

        // 반드시 save 결과를 받아서 id 확보
        MemberProfileImage saved = memberProfileImageRepository.save(toSave);

        member.changeProfileImage(saved.getId());
        memberRepository.save(member);

        return new UploadProfileImageResponseDTO(saved.getUrl(), saved.getId());
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException();
        }
        if (file.getSize() > MAX_BYTES) {
            throw new FileTooLargeException(MAX_BYTES);
        }
        String ct = normalizeContentType(file.getContentType());
        if (!ALLOWED_CT.contains(ct)) {
            throw new UnsupportedMediaTypeException(ct, ALLOWED_CT);
        }

        // (선택) 시그니처 검사까지 하고 싶으면 여기서 magic number 체크 추가
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        // JPEG: FF D8 ... FF D9
        // WEBP: "RIFF"...."WEBP"
    }

    private String normalizeContentType(String ct) {
        if (ct == null) return "";
        // 일부 브라우저/라이브러리가 image/jpg로 줄 수도 있어서 normalize
        if (ct.equalsIgnoreCase("image/jpg")) return "image/jpeg";
        return ct.toLowerCase(Locale.ROOT);
    }

    private String contentTypeToExt(String ct) {
        return switch (ct) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new UnsupportedMediaTypeException(ct, ALLOWED_CT);
        };
    }
}
