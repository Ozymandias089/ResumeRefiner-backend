package com.resumerefiner.resumerefinerbackend.resume.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.PreconditionFailedException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.PreconditionRequiredException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.internal.*;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.request.UpdateResumeRequestDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.UpdateResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.*;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateResumeService implements UpdateResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public long patch(UpdateResumeCommand command) {
        // 1) Handle -> memberId
        long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid handle"));

        // 2) slug -> resume
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // 3) owner check
        if (!Objects.equals(resume.getMemberId(), memberId)) {
            log.error("Resume owner id mismatch. handle={}", command.handle());
            throw new InvalidCredentialsException("Invalid member");
        }

        // 4) If-Match required
        Long expected = command.expectedVersion();
        if (expected == null) {
            log.error("If-Match not found");
            throw new PreconditionRequiredException("If-Match required");
        }

        // 5) precondition check (etag mismatch)
        long current = resume.getVersion();
        if (current != expected) {
            log.error("ETag mismatch. expected={}, current={}", expected, current);
            throw new PreconditionFailedException("Etag mismatch", current);
        }

        // 6) apply patch
        applyPatch(resume, command.request());

        try {
            // save 시점에 버전 증가
            Resume saved = resumeRepository.save(resume);
            return saved.getVersion();
        } catch (OptimisticLockException e) {
            // 사전 체크 통과 후에도 경합으로 터질 수 있음
            throw new PreconditionFailedException("Concurrent update", -1L);
        }
    }

    private void applyPatch(Resume resume, UpdateResumeRequestDTO dto) {
        if (dto == null) return;

        if (dto.title() != null) resume.changeTitle(dto.title());
        if (dto.languageCode() != null) resume.changeLanguage(dto.languageCode());

        // profile: 도메인 merge 사용 (null은 '변경 없음')
        if (dto.profile() != null) {
            ResumeProfile merged = resume.getProfile().merge(
                    dto.profile().name(),
                    dto.profile().email(),
                    dto.profile().phone(),
                    dto.profile().location(),
                    dto.profile().birthDate(),
                    dto.profile().gender()
            );
            resume.changeProfile(merged);
        }

        // military: clear 우선(플래그), 아니면 merge
        if (dto.clearMilitaryService()) {
            resume.clearMilitaryService();
        } else if (dto.militaryService() != null) {
            MilitaryService merged = mergeMilitary(resume.getMilitaryService(), dto.militaryService());
            resume.changeMilitaryService(merged);
        }

        // arrays: 존재하면 replace-only (빈 배열이면 전체 삭제)
        if (dto.educations() != null) {
            resume.replaceEducations(dto.educations().stream().map(this::toEducationVo).toList());
        }
        if (dto.experiences() != null) {
            resume.replaceExperiences(dto.experiences().stream().map(this::toExperienceVo).toList());
        }
        if (dto.customSections() != null) {
            resume.replaceCustomSections(dto.customSections().stream().map(this::toCustomSectionVo).toList());
        }
    }

    private MilitaryService mergeMilitary(MilitaryService base, MilitaryServicePatchDTO patch) {
        // base는 null일 수 있음
        MilitaryStatus status = patch.militaryStatus() != null
                ? patch.militaryStatus()
                : (base != null ? base.getStatus() : null);

        MilitaryBranch branch = patch.branch() != null
                ? patch.branch()
                : (base != null ? base.getBranch() : null);

        String period = patch.period() != null
                ? patch.period()
                : (base != null ? base.getPeriod() : null);

        String rank = patch.rank() != null
                ? patch.rank()
                : (base != null ? base.getRank() : null);

        String notes = patch.notes() != null
                ? patch.notes()
                : (base != null ? base.getNotes() : null);

        // militaryService를 "생성/유지"하려면 status는 필수
        if (status == null) {
            throw new com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException(
                    "MILITARY_STATUS_REQUIRED"
            );
        }

        return MilitaryService.of(status, branch, period, rank, notes);
    }

    private ResumeEducation toEducationVo(EducationDTO d) {
        Objects.requireNonNull(d);
        return ResumeEducation.of(
                d.schoolName(),
                d.major(),
                d.degree(),
                d.period(),
                d.description(),
                d.displayOrder()
        );
    }

    private ResumeExperience toExperienceVo(ExperienceDTO d) {
        Objects.requireNonNull(d);
        return ResumeExperience.of(
                d.company(),
                d.role(),
                d.period(),
                d.description(),
                d.displayOrder()
        );
    }

    private ResumeCustomSection toCustomSectionVo(CustomSectionDTO d) {
        Objects.requireNonNull(d);
        return ResumeCustomSection.of(
                d.type(),
                d.subject(),
                d.content(),
                d.displayOrder()
        );
    }
}
