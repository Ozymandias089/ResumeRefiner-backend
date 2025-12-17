package com.resumerefiner.resumerefinerbackend.resume.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "resume",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resume_slug", columnNames = "slug")
        },
        indexes = {
                @Index(name = "idx_resume_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume extends BaseTimeEntity implements AggregateRoot {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member와 연관관계는 ID로만 느슨하게 가져간다
    @Getter
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // Shared link slug
    @Getter
    @Embedded
    private ResumeSlug slug;

    @Getter
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10)
    private LanguageCode languageCode;

    @Getter
    @Column(name = "photo_image_id")
    private Long photoImageId;

    /* ---------------------------
     * Structured VO (single)
     * --------------------------- */

    @Getter
    @Embedded
    private ResumeProfile profile;

    @Getter
    @Embedded
    private MilitaryService militaryService; // optional (null 가능)

    /* ---------------------------
     * Structured VO collections
     * --------------------------- */

    @Getter
    @ElementCollection
    @CollectionTable(
            name = "resume_education",
            joinColumns = @JoinColumn(name = "resume_id")
    )
    @OrderBy("displayOrder ASC")
    private List<ResumeEducation> educations = new ArrayList<>();

    @Getter
    @ElementCollection
    @CollectionTable(
            name = "resume_experience",
            joinColumns = @JoinColumn(name = "resume_id")
    )
    @OrderBy("displayOrder ASC")
    private List<ResumeExperience> experiences = new ArrayList<>();

    @Getter
    @ElementCollection
    @CollectionTable(
            name = "resume_custom_section",
            joinColumns = @JoinColumn(name = "resume_id")
    )
    @OrderBy("displayOrder ASC")
    private List<ResumeCustomSection> customSections = new ArrayList<>();

    /* ---------------------------
     * Factory (예시)
     * --------------------------- */

    @Builder(access = AccessLevel.PRIVATE)
    private Resume(
            Long memberId,
            String title,
            LanguageCode languageCode,
            ResumeProfile profile,
            MilitaryService militaryService,
            List<ResumeEducation> educations,
            List<ResumeExperience> experiences,
            List<ResumeCustomSection> customSections
    ) {
        if (memberId == null) throw new IllegalArgumentException("memberId required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
        if (languageCode == null) throw new IllegalArgumentException("languageCode required");
        if (profile == null) throw new IllegalArgumentException("profile required");
        if (militaryService == null) throw new IllegalArgumentException("militaryService required");

        this.memberId = memberId;
        this.slug = ResumeSlug.random();
        this.title = title;
        this.languageCode = languageCode;
        this.profile = profile;
        this.militaryService = militaryService;
        this.educations = educations == null ? List.of() : List.copyOf(educations);
        this.experiences = experiences == null ? List.of() : List.copyOf(experiences);
        this.customSections = customSections == null ? List.of() : List.copyOf(customSections);
    }

    public static Resume create(
            Long memberId,
            String title,
            LanguageCode languageCode,
            ResumeProfile resumeProfile,
            MilitaryService military,
            List<ResumeEducation> educations,
            List<ResumeExperience> experiences,
            List<ResumeCustomSection> customSections
    ) {
        return Resume.builder()
                .memberId(memberId)
                .title(title)
                .languageCode(languageCode)
                .profile(resumeProfile)
                .militaryService(military)
                .educations(educations)
                .experiences(experiences)
                .customSections(customSections)
                .build();
    }

    /* ---------------------------
     * Domain actions (replace style)
     * --------------------------- */

    public void changeTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("RESUME_TITLE_REQUIRED");
        this.title = title.trim();
    }

    public void changeLanguage(LanguageCode languageCode) {
        if (languageCode == null) throw new IllegalArgumentException("RESUME_LANGUAGE_REQUIRED");
        this.languageCode = languageCode;
    }

    public void changePhotoImageId(Long photoImageId) {
        this.photoImageId = photoImageId;
    }

    public void changeProfile(ResumeProfile profile) {
        if (profile == null) throw new IllegalArgumentException("RESUME_PROFILE_REQUIRED");
        this.profile = profile;
    }

    public void clearProfile() {
        // 정책적으로 profile은 필수로 두고 싶으면 이 메서드는 제거해도 됨
        this.profile = ResumeProfile.ofName("익명");
    }

    public void changeMilitaryService(MilitaryService militaryService) {
        this.militaryService = militaryService; // null 허용
    }

    public void clearMilitaryService() {
        this.militaryService = null;
    }

    public void replaceEducations(List<ResumeEducation> educations) {
        this.educations = (educations == null) ? new ArrayList<>() : new ArrayList<>(educations);
        // 여기서 displayOrder 검증을 넣고 싶으면 validateOrders(...) 추가
    }

    public void replaceExperiences(List<ResumeExperience> experiences) {
        this.experiences = (experiences == null) ? new ArrayList<>() : new ArrayList<>(experiences);
    }

    public void replaceCustomSections(List<ResumeCustomSection> customSections) {
        this.customSections = (customSections == null) ? new ArrayList<>() : new ArrayList<>(customSections);
    }
}
