package com.resumerefiner.resumerefinerbackend.media.domain.profile;

import com.resumerefiner.resumerefinerbackend.media.domain.AbstractImageFile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "member_profile_image",
        indexes = @Index(name = "idx_member_profile_image_member_id", columnList = "member_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileImage extends AbstractImageFile {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private MemberProfileImage(Long memberId, String url, String fileName, String contentType, long sizeBytes) {
        super(url, fileName, contentType, sizeBytes);
        this.memberId = memberId;
    }

    public static MemberProfileImage create(Long memberId, String url, String fileName, String contentType, long sizeBytes) {
        return new MemberProfileImage(memberId, url, fileName, contentType, sizeBytes);
    }
}
