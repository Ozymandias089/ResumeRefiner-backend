package com.resumerefiner.resumerefinerbackend.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Getter
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid email format: " + value);
        }
        this.value = value.toLowerCase(); // 보통 lowercase normalize 많이 함
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
