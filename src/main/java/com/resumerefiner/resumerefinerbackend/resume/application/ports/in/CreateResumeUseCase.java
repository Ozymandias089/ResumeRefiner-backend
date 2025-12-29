package com.resumerefiner.resumerefinerbackend.resume.application.ports.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.LanguageCode;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.*;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public interface CreateResumeUseCase {

    String create(CreateResumeCommand command);

    @Builder
    record CreateResumeCommand(
            Handle handle,
            String title,
            LanguageCode languageCode,
            ResumeProfileCommand profile,
            MilitaryServiceCommand military,
            List<EducationCommand> educations,
            List<ExperienceCommand> experiences,
            List<CustomSectionCommand> customSections
    ) {}

    @Builder
    record ResumeProfileCommand(
            String name,
            Gender gender,
            String email,
            String phone,
            String location,
            LocalDate birthDate
    ) {}

    @Builder
    record MilitaryServiceCommand(
            MilitaryStatus status,
            MilitaryBranch branch,
            String period,
            String rank,
            String notes
    ) {}

    @Builder
    record EducationCommand(
            String schoolName,
            String major,
            EducationDegree degree,
            String period,
            String description,
            int displayOrder
    ) {}

    @Builder
    record ExperienceCommand(
            String company,
            String role,
            String period,
            String description,
            Integer displayOrder
    ) {}

    @Builder
    record CustomSectionCommand(
            ResumeSectionType type,
            String subject,
            String content,
            Integer displayOrder
    ) {}
}
