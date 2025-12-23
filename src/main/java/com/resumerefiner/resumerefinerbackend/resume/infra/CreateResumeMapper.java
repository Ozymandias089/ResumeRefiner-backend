package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.application.dto.request.CreateResumeRequestDTO;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.CreateResumeUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateResumeMapper {

    public CreateResumeUseCase.CreateResumeCommand toCommand(Handle handle, CreateResumeRequestDTO dto) {
        return CreateResumeUseCase.CreateResumeCommand.builder()
                .handle(handle)
                .title(dto.title())
                .languageCode(dto.languageCode())
                .profile(mapProfile(dto))
                .military(mapMilitary(dto))
                .educations(mapEducations(dto))
                .experiences(mapExperiences(dto))
                .customSections(mapCustom(dto))
                .build();
    }

    private CreateResumeUseCase.ResumeProfileCommand mapProfile(CreateResumeRequestDTO dto) {
        var p = dto.profile();
        return CreateResumeUseCase.ResumeProfileCommand.builder()
                .name(p.name())
                .gender(p.gender())
                .email(p.email())
                .phone(p.phone())
                .location(p.location())
                .birthDate(p.birthDate())
                .build();
    }

    private CreateResumeUseCase.MilitaryServiceCommand mapMilitary(CreateResumeRequestDTO dto) {
        if (dto.military() == null) return null;
        var m = dto.military();
        return CreateResumeUseCase.MilitaryServiceCommand.builder()
                .status(m.militaryStatus())
                .branch(m.branch() == null ? null : m.branch())
                .period(m.period())
                .rank(m.rank())
                .notes(m.notes())
                .build();
    }

    private List<CreateResumeUseCase.EducationCommand> mapEducations(CreateResumeRequestDTO dto) {
        return dto.education().stream()
                .map(e -> CreateResumeUseCase.EducationCommand.builder()
                        .schoolName(e.schoolName())
                        .major(e.major())
                        .degree(e.degree())
                        .period(e.period())
                        .description(e.description())
                        .description(e.description())
                        .displayOrder(e.displayOrder())
                        .build()
                ).toList();
    }

    private List<CreateResumeUseCase.ExperienceCommand> mapExperiences(CreateResumeRequestDTO dto) {
        if (dto.experiences() == null) return List.of();
        return dto.experiences().stream()
                .map(e -> CreateResumeUseCase.ExperienceCommand.builder()
                        .company(e.company())
                        .role(e.role())
                        .period(e.period())
                        .description(e.description())
                        .displayOrder(e.displayOrder())
                        .build())
                .toList();
    }

    private List<CreateResumeUseCase.CustomSectionCommand> mapCustom(CreateResumeRequestDTO dto) {
        if (dto.custom() == null) return List.of();
        return dto.custom().stream()
                .map(c -> CreateResumeUseCase.CustomSectionCommand.builder()
                        .type(c.type())
                        .subject(c.subject())
                        .content(c.content())
                        .displayOrder(c.displayOrder())
                        .build())
                .toList();
    }
}