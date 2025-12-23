package com.resumerefiner.resumerefinerbackend.resume.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeProfile implements ValueObject {

    private static final int NAME_MAX = 100;
    private static final int PHONE_MAX = 30;
    private static final int LOCATION_MAX = 255;

    private static final LocalDate BIRTHDATE_MIN = LocalDate.of(1900, 1, 1);

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

    @Getter
    @Column(name = "profile_birth_date") // Postgres: DATE 로 매핑됨
    private LocalDate birthDate; // optional

    @Getter
    @Column(name = "profile_gender", nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder(access = AccessLevel.PRIVATE)
    private ResumeProfile(String name, ContactEmail email, String phone, String location, LocalDate birthDate, Gender gender) {
        this.name = normalizeRequiredName(name);
        this.email = email; // ContactEmail이 검증 책임
        this.phone = normalizeOptional(phone, PHONE_MAX);
        this.location = normalizeOptional(location, LOCATION_MAX);
        this.birthDate = validateBirthDate(birthDate);
        this.gender = gender;
    }

    /* ---------------------------
     * Static factories
     * --------------------------- */

    public static ResumeProfile ofName(String name) {
        return ResumeProfile.builder()
                .name(name)
                .build();
    }

    public static ResumeProfile of(String name, String email, String phone, String location, LocalDate birthDate) {
        return ResumeProfile.builder()
                .name(name)
                .email(email == null ? null : ContactEmail.of(email))
                .phone(phone)
                .location(location)
                .birthDate(birthDate)
                .build();
    }

    public static ResumeProfile anonymous() {
        return ResumeProfile.builder()
                .name("익명")
                .build();
    }

    public static ResumeProfile fromNullable(
            String name,
            Gender gender,
            String email,
            String phone,
            String location,
            LocalDate birthDate
    ) {
        // name이 없으면 익명 프로필로 degrade
        if (name == null || name.isBlank()) return anonymous();

        return ResumeProfile.builder()
                .name(name)
                .gender(gender)
                .email(email == null ? null : ContactEmail.of(email)) // null-safe
                .phone(phone)
                .location(location)
                .birthDate(birthDate)
                .build();
    }


    /* ---------------------------
     * Domain actions
     * --------------------------- */

    public ResumeProfile changeName(String newName) {
        return new ResumeProfile(newName, this.email, this.phone, this.location, this.birthDate, this.gender);
    }

    public ResumeProfile changeEmail(String newEmail) {
        return new ResumeProfile(this.name, ContactEmail.of(newEmail), this.phone, this.location, this.birthDate, this.gender);
    }

    public ResumeProfile clearEmail() {
        return new ResumeProfile(this.name, null, this.phone, this.location, this.birthDate, this.gender);
    }

    public ResumeProfile changePhone(String newPhone) {
        return new ResumeProfile(this.name, this.email, newPhone, this.location, this.birthDate, this.gender);
    }

    public ResumeProfile clearPhone() {
        return new ResumeProfile(this.name, this.email, null, this.location, this.birthDate, this.gender);
    }

    public ResumeProfile changeLocation(String newLocation) {
        return new ResumeProfile(this.name, this.email, this.phone, newLocation, this.birthDate, this.gender);
    }

    public ResumeProfile clearLocation() {
        return new ResumeProfile(this.name, this.email, this.phone, null, this.birthDate, this.gender);
    }

    public ResumeProfile changeBirthDate(LocalDate newBirthDate) {
        return new ResumeProfile(this.name, this.email, this.phone, this.location, newBirthDate, this.gender);
    }

    public ResumeProfile clearBirthDate() {
        return new ResumeProfile(this.name, this.email, this.phone, this.location, null, this.gender);
    }

    public ResumeProfile redactContactInfo() {
        return new ResumeProfile(this.name, null, null, null, null, this.gender);
    }

    public ResumeProfile merge(
            String name,
            String email,
            String phone,
            String location,
            LocalDate birthDate,
            Gender gender
    ) {
        return new ResumeProfile(
                name != null ? name : this.name,
                email != null ? ContactEmail.of(email) : this.email,
                phone != null ? phone : this.phone,
                location != null ? location : this.location,
                birthDate != null ? birthDate : this.birthDate,
                gender != null ? gender : this.gender
        );
    }

    /* ---------------------------
     * Validation & normalization
     * --------------------------- */

    private static String normalizeRequiredName(String raw) {
        String v = normalizeOptional(raw, NAME_MAX);
        if (v == null || v.isBlank()) {
            throw new DomainRuleViolationException("PROFILE_NAME_REQUIRED");
        }
        return v;
    }

    private static String normalizeOptional(String raw, int maxLen) {
        if (raw == null) return null;
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) return null;
        if (v.length() > maxLen) {
            throw new DomainConflictException("VALUE_TOO_LONG");
        }
        return v;
    }

    private static LocalDate validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) return null;

        LocalDate today = LocalDate.now(); // 도메인에서 Zone 고정하고 싶으면 Clock 주입 설계로 빼도 됨
        if (birthDate.isAfter(today)) {
            throw new DomainRuleViolationException("BIRTH_DATE_IN_FUTURE");
        }
        if (birthDate.isBefore(BIRTHDATE_MIN)) {
            throw new DomainRuleViolationException("BIRTH_DATE_TOO_OLD");
        }
        return birthDate;
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
