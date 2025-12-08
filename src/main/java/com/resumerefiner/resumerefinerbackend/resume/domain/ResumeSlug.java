package com.resumerefiner.resumerefinerbackend.resume.domain;

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
    @Getter
    @Column(name = "slug", nullable = false, length = 64, unique = true)
    private String value;

    private ResumeSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("slug must not be blank");
        }
        if (value.length() > 64) {
            throw new IllegalArgumentException("slug exceeds length limit");
        }
        this.value = value;
    }

    public static ResumeSlug of(String value) {
        return new ResumeSlug(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
