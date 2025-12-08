package com.resumerefiner.resumerefinerbackend.media.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "media_file",
        indexes = {
                @Index(name = "idx_media_file_owner", columnList = "owner_id, owner_type")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaFile extends BaseTimeEntity implements AggregateRoot {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 업로드한 주체 / 소유자 ID
     * - MEMBER: member.id
     * - RESUME: resume.id
     */
    @Getter
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private MediaOwnerType ownerType;

    @Getter
    @Column(name = "url", nullable = false, length = 512)
    private String url;

    @Getter
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Getter
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Getter
    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    private MediaFile(Long ownerId,
                      MediaOwnerType ownerType,
                      String url,
                      String fileName,
                      String contentType,
                      long sizeBytes) {

        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
        this.ownerType = Objects.requireNonNull(ownerType, "ownerType must not be null");

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType must not be blank");
        }
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("sizeBytes must be >= 0");
        }

        this.url = url;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    // ==== 정적 팩토리 메서드 ====

    public static MediaFile forMember(Long memberId, String url, String fileName, String contentType, long sizeBytes) {
        return new MediaFile(
                memberId,
                MediaOwnerType.MEMBER,
                url,
                fileName,
                contentType,
                sizeBytes
        );
    }

    public static MediaFile forResume(Long resumeId, String url, String fileName, String contentType, long sizeBytes) {
        return new MediaFile(
                resumeId,
                MediaOwnerType.RESUME,
                url,
                fileName,
                contentType,
                sizeBytes
        );
    }

    // ==== 비즈니스 메서드 (필요하면) ====

    public void changeUrl(String newUrl) {
        if (newUrl == null || newUrl.isBlank()) {
            throw new IllegalArgumentException("url must not be blank");
        }
        this.url = newUrl;
    }
}
