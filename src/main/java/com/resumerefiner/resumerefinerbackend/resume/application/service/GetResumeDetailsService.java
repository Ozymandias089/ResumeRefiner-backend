package com.resumerefiner.resumerefinerbackend.resume.application.service;

import com.resumerefiner.resumerefinerbackend.media.domain.MediaFileRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetResumeDetailsService implements GetResumeDetailsUseCase {
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final MediaFileRepository mediaFileRepository;

    @Override
    @Transactional(readOnly = true)
    public GetResumeResponseDTO getResumeDetails(GetResumeDetailsCommand command) {
        Long id = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        log.debug("Get resume details for member {}", command.handle().toString());

        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new IllegalArgumentException("Resume Not Found"));
        log.debug("Get resume details for member {}", command.handle().toString());

        if (!resume.getMemberId().equals(id)) {
            log.warn("Illegal Access Detected");
            throw new IllegalArgumentException("FORBIDDEN");
        }

        String imageUrl = resume.getPhotoImageId() == null
                ? null
                : mediaFileRepository.findUrlById(resume.getPhotoImageId()).orElse(null);

        var rp = resume.getProfile();
        ProfileDTO profile = new ProfileDTO(
                rp.getName(),
                safeToString(rp.getEmail()),   // email optional
                rp.getPhone(),
                rp.getLocation()
        );

        MilitaryServiceDTO military = null;
        if (resume.getMilitaryService() != null) {
            var ms = resume.getMilitaryService();
            military = new MilitaryServiceDTO(
                    safeToString(ms.getStatus()),
                    safeToString(ms.getBranch()),
                    ms.getPeriod(),
                    ms.getRank(),
                    ms.getNotes()
            );
        }

        List<EducationDTO> educations = resume.getEducations().stream()
                .map(e -> new EducationDTO(
                        e.getSchoolName(),
                        e.getMajor(),
                        safeToString(e.getDegree()),  // degree optional
                        e.getPeriod(),
                        e.getDescription(),
                        e.getDisplayOrder()
                ))
                .toList();

        List<ExperienceDTO> experiences = resume.getExperiences().stream()
                .map(x -> new ExperienceDTO(
                        x.getCompany(),
                        x.getRole(),
                        x.getPeriod(),
                        x.getDescription(),
                        x.getDisplayOrder()
                ))
                .toList();

        List<CustomSectionDTO> customSections = resume.getCustomSections().stream()
                .map(c -> new CustomSectionDTO(
                        safeToString(c.getType()),   // type optional
                        c.getSubject(),
                        c.getContent(),
                        c.getDisplayOrder()
                ))
                .toList();

        return GetResumeResponseDTO.builder()
                .slug(resume.getSlug().getValue()) // ✅ toString보다 value 권장
                .title(resume.getTitle())
                .createdAt(resume.getCreatedAt())
                .modifiedAt(resume.getUpdatedAt())
                .languageCode(resume.getLanguageCode())
                .photoUrl(imageUrl)
                .profile(profile)
                .military(military)
                .educations(educations)
                .experiences(experiences)
                .customSections(customSections)
                .build();
    }

    private static String safeToString(Object v) {
        return v == null ? null : v.toString();
    }
}
