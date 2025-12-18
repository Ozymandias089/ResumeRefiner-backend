package com.resumerefiner.resumerefinerbackend.resume.application.service;

import com.resumerefiner.resumerefinerbackend.media.domain.MediaFileRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.response.GetResumeSummaryListResponseDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.GetResumeDetailsUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.PageResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSummaryProjection;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeSort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetResumeDetailsService implements GetResumeDetailsUseCase, PageResumeUseCase {
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

    @Override
    @Transactional(readOnly = true)
    public GetResumeSummaryListResponseDTO getResumeSummaryList(GetResumeSummaryCommand command) {
        // Fetch Member ID with Handle
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // Create Pageable Object
        ResumeSort sort = command.sort() != null ? command.sort() : ResumeSort.UPDATED_AT_DESC;
        int page = Math.max(command.page(), 0);
        int size = Math.min(Math.max(command.size(), 1), 50);
        Pageable pageable = PageRequest.of(page, size, ResumeSort.toSort(sort));

        String q = command.q();
        q = (q == null) ? null : q.trim();

        // Query Summary Projection with id, pageable
        Page<ResumeSummaryProjection> resumes = resumeRepository.findResumeSummaries(memberId, q, pageable);

        // Map to DTO
        return GetResumeSummaryListResponseDTO.builder()
                .resumes(
                        resumes.getContent().stream()
                                .map(e -> ResumeSummaryDTO.builder()
                                        .slug(e.slug().toString())
                                        .title(e.title())
                                        .createdAt(e.createdAt())
                                        .updatedAt(e.updatedAt())
                                        .reviewCount(e.reviewCount())
                                        .build()
                                ).toList()
                )
                .page(resumes.getNumber())
                .size(resumes.getSize())
                .totalElements(resumes.getTotalElements())
                .hasPrev(resumes.hasPrevious())
                .hasNext(resumes.hasNext())
                .build();
    }

    private static String safeToString(Object v) {
        return v == null ? null : v.toString();
    }
}
