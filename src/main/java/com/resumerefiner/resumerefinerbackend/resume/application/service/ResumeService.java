package com.resumerefiner.resumerefinerbackend.resume.application.service;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.CreateResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.*;
import com.resumerefiner.resumerefinerbackend.resume.infra.ResumeAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService implements CreateResumeUseCase {
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;
    private final ResumeAssembler resumeAssembler;

    @Override
    @Transactional
    public String create(CreateResumeCommand command) {
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        log.debug("Getting Id for user {}", command.handle().toString());

        ResumeProfile profile = resumeAssembler.toProfile(command.profile());
        log.debug("Mapping Resume Profile");
        MilitaryService military = resumeAssembler.toMilitary(command.military());
        log.debug("Mapping Military Profile");
        List<ResumeEducation> educations = resumeAssembler.toEducations(command.educations());
        log.debug("Mapping Resume Educations");
        List<ResumeExperience> experiences = resumeAssembler.toExperiences(command.experiences());
        log.debug("Mapping Resume Experiences");
        List<ResumeCustomSection> customSections = resumeAssembler.toCustomSections(command.customSections());
        log.debug("Mapping Resume Custom Sections");

        Resume resume = Resume.create(
                memberId,
                command.title(),
                command.languageCode(),
                profile,
                military,
                educations,
                experiences,
                customSections
        );
        log.debug("Mapping Resume to Resume");

        return resumeRepository.save(resume).getSlug().toString();
    }
}

