package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.util.ResumeSlugGenerator;
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
        String v = value == null ? "" : value.trim();
        if (v.isBlank()) throw new IllegalArgumentException("slug must not be blank");
        if (v.length() > MAX_LENGTH) throw new IllegalArgumentException("slug exceeds length limit");
        this.value = v;
    }

    public static ResumeSlug random() {
        return new ResumeSlug(ResumeSlugGenerator.generate());
    }

    public static ResumeSlug of(String value) {
        return new ResumeSlug(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
