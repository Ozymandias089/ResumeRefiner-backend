package com.resumerefiner.resumerefinerbackend.media.domain.resume;

import com.resumerefiner.resumerefinerbackend.media.domain.AbstractImageFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "resume_image",
        indexes = @Index(name = "idx_resume_image_resume_id", columnList = "resume_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeImage extends AbstractImageFile {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    private ResumeImage(Long resumeId, String url, String fileName, String contentType, long sizeBytes) {
        super(url, fileName, contentType, sizeBytes);
        this.resumeId = resumeId;
    }

    public static ResumeImage create(Long resumeId, String url, String fileName, String contentType, long sizeBytes) {
        return new ResumeImage(resumeId, url, fileName, contentType, sizeBytes);
    }
}
