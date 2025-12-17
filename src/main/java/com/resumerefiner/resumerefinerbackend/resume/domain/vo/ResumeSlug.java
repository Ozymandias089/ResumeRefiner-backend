package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeSlug implements ValueObject {
    private static final int MAX_LENGTH = 64;
    @Getter
    @Column(name = "slug", nullable = false, length = MAX_LENGTH, unique = true)
    private String value;

    private ResumeSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("slug must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("slug exceeds length limit");
        }
        this.value = value;
    }

    /** 외부 입력용 (정말 필요할 때만) */
    public static ResumeSlug of(String value) {
        return new ResumeSlug(value);
    }

    /** 랜덤 생성용 (주 사용 경로) */
    public static ResumeSlug random() {
//        return new ResumeSlug(ResumeSlugGenerator.generate());
    }

    @Override
    public String toString() {
        return value;
    }
}
