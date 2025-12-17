package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeProfile implements ValueObject {

    private static final int NAME_MAX = 100;
    private static final int PHONE_MAX = 30;
    private static final int LOCATION_MAX = 255;

    @Getter
    @Column(name = "profile_name", nullable = false, length = NAME_MAX)
    private String name;

    @Getter
    @Embedded
    private ContactEmail email; // optional VO

    @Getter
    @Column(name = "profile_phone", length = PHONE_MAX)
    private String phone; // optional

    @Getter
    @Column(name = "profile_location", length = LOCATION_MAX)
    private String location; // optional

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeProfile(String name, ContactEmail email, String phone, String location) {
        this.name = normalizeRequiredName(name);
        this.email = email; // ContactEmail이 검증 책임
        this.phone = normalizeOptional(phone, PHONE_MAX);
        this.location = normalizeOptional(location, LOCATION_MAX);
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static ResumeProfile ofName(String name) {
        return ResumeProfile.builder()
                .name(name)
                .build();
    }

    public static ResumeProfile of(String name, String email, String phone, String location) {
        return ResumeProfile.builder()
                .name(name)
                .email(email == null ? null : ContactEmail.of(email))
                .phone(phone)
                .location(location)
                .build();
    }

    public static ResumeProfile anonymous() {
        return ResumeProfile.builder()
                .name("익명")
                .build();
    }

    public static ResumeProfile fromNullable(
            String name,
            String email,
            String phone,
            String location
    ) {
        // name이 없으면 익명 프로필로 degrade
        if (name == null || name.isBlank()) return anonymous();

        return ResumeProfile.builder()
                .name(name)
                .email(email == null ? null : ContactEmail.of(email)) // null-safe
                .phone(phone)
                .location(location)
                .build();
    }


    /* ---------------------------
     * Domain actions
     * --------------------------- */

    public ResumeProfile changeName(String newName) {
        return new ResumeProfile(newName, this.email, this.phone, this.location);
    }

    public ResumeProfile changeEmail(String newEmail) {
        return new ResumeProfile(this.name, ContactEmail.of(newEmail), this.phone, this.location);
    }

    public ResumeProfile clearEmail() {
        return new ResumeProfile(this.name, null, this.phone, this.location);
    }

    public ResumeProfile changePhone(String newPhone) {
        return new ResumeProfile(this.name, this.email, newPhone, this.location);
    }

    public ResumeProfile clearPhone() {
        return new ResumeProfile(this.name, this.email, null, this.location);
    }

    public ResumeProfile changeLocation(String newLocation) {
        return new ResumeProfile(this.name, this.email, this.phone, newLocation);
    }

    public ResumeProfile clearLocation() {
        return new ResumeProfile(this.name, this.email, this.phone, null);
    }

    public ResumeProfile redactContactInfo() {
        return new ResumeProfile(this.name, null, null, null);
    }

    public ResumeProfile merge(
            String name,
            String email,
            String phone,
            String location
    ) {
        return new ResumeProfile(
                name != null ? name : this.name,
                email != null ? ContactEmail.of(email) : this.email,
                phone != null ? phone : this.phone,
                location != null ? location : this.location
        );
    }

    /* ---------------------------
     * Validation & normalization
     * --------------------------- */

    private static String normalizeRequiredName(String raw) {
        String v = normalizeOptional(raw, NAME_MAX);
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException("PROFILE_NAME_REQUIRED");
        }
        return v;
    }

    private static String normalizeOptional(String raw, int maxLen) {
        if (raw == null) return null;
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) return null;
        if (v.length() > maxLen) {
            throw new IllegalArgumentException("VALUE_TOO_LONG");
        }
        return v;
    }

    /* ---------------------------
     * Value semantics
     * --------------------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResumeProfile that)) return false;
        return Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(phone, that.phone)
                && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, phone, location);
    }
}
