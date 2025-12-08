package com.resumerefiner.resumerefinerbackend.resume.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false, length = 10)
    private LanguageCode languageCode;

    @Getter
    @Column(name = "photo_image_id")
    private Long photoImageId;

    private Resume(Long memberId,
                   ResumeSlug slug,
                   String title,
                   String originalText,
                   LanguageCode languageCode,
                   Long photoImageId) {

        if (memberId == null) throw new IllegalArgumentException("memberId must not be null");
        if (slug == null) throw new IllegalArgumentException("slug must not be null");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title must not be blank");
        if (originalText == null) throw new IllegalArgumentException("originalText must not be null");
        if (languageCode == null) throw new IllegalArgumentException("languageCode must not be null");

        this.memberId = memberId;
        this.slug = slug;
        this.title = title;
        this.originalText = originalText;
        this.languageCode = languageCode;
        this.photoImageId = photoImageId;
    }

    // ==== 정적 팩토리 ====

    public static Resume create(Long memberId, String slug, String title, String originalText, LanguageCode languageCode, Long photoImageId) {
        return new Resume(
                memberId,
                ResumeSlug.of(slug),
                title,
                originalText,
                languageCode,
                photoImageId
        );
    }

    // ==== 비즈니스 메서드 ====

    public void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("title cannot be empty");
        }
        this.title = newTitle;
    }

    public void changePhoto(Long mediaFileId) {
        this.photoImageId = mediaFileId;
    }

    public void updateOriginalText(String newText) {
        if (newText == null) throw new IllegalArgumentException("text cannot be null");
        this.originalText = newText;
    }
}
