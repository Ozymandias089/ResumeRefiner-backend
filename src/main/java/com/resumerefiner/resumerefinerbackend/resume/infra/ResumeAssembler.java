package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.resume.application.ports.in.CreateResumeUseCase;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumeAssembler {

    public ResumeProfile toProfile(CreateResumeUseCase.ResumeProfileCommand p) {
        if (p == null) return ResumeProfile.anonymous();
        return ResumeProfile.fromNullable(p.name(), p.email(), p.phone(), p.location());
    }

    public MilitaryService toMilitary(CreateResumeUseCase.MilitaryServiceCommand m) {
        if (m == null) return null;
        return MilitaryService.fromNullable(m.status(), m.branch(), m.period(), m.rank(), m.notes());
    }

    public List<ResumeEducation> toEducations(List<CreateResumeUseCase.EducationCommand> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(e -> {
                    boolean minimal = e.major() == null && e.degree() == null
                            && isBlank(e.period()) && isBlank(e.description());
                    return minimal
                            ? ResumeEducation.ofMinimal(e.schoolName(), e.displayOrder())
                            : ResumeEducation.of(e.schoolName(), e.major(), e.degree(), e.period(), e.description(), e.displayOrder());
                })
                .toList();
    }

    public List<ResumeExperience> toExperiences(List<CreateResumeUseCase.ExperienceCommand> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(x -> isBlank(x.description())
                        ? ResumeExperience.ofMinimal(x.company(), x.role(), x.period(), x.displayOrder())
                        : ResumeExperience.of(x.company(), x.role(), x.period(), x.description(), x.displayOrder()))
                .toList();
    }

    public List<ResumeCustomSection> toCustomSections(List<CreateResumeUseCase.CustomSectionCommand> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(c -> c.type() == null
                        ? ResumeCustomSection.ofMinimal(c.subject(), c.content(), c.displayOrder())
                        : ResumeCustomSection.of(c.type(), c.subject(), c.content(), c.displayOrder()))
                .toList();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
