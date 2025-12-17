package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ContactEmail implements ValueObject {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Column(name = "email", length = 255)
    private String value; // nullable 허용

    private ContactEmail(String value) {
        this.value = normalize(value);
    }

    public static ContactEmail of(String value) {
        return new ContactEmail(value);
    }

    public static ContactEmail empty() {
        return new ContactEmail(null);
    }

    public boolean isPresent() {
        return value != null;
    }

    public String value() {
        return value;
    }

    private static String normalize(String raw) {
        if (raw == null) return null;

        String v = raw.trim().toLowerCase();
        if (v.isEmpty()) return null;

        if (!EMAIL_PATTERN.matcher(v).matches()) {
            throw new IllegalArgumentException("invalid contact email format");
        }
        return v;
    }

    @Override
    public String toString() {
        return value;
    }
}
