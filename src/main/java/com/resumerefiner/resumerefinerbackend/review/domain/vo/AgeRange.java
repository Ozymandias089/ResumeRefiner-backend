package com.resumerefiner.resumerefinerbackend.review.domain.vo;

import java.time.LocalDate;
import java.time.Period;

public enum AgeRange {
    UNKNOWN,
    AGE_15_19,
    AGE_20_24,
    AGE_25_29,
    AGE_30_34,
    AGE_35_39,
    AGE_40_44,
    AGE_45_49,
    AGE_50_PLUS;

    public static AgeRange fromBirthDate(LocalDate birthDate) {
        if (birthDate == null) return UNKNOWN;
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 0) return UNKNOWN;

        if (age <= 19) return AGE_15_19;
        if (age <= 24) return AGE_20_24;
        if (age <= 29) return AGE_25_29;
        if (age <= 34) return AGE_30_34;
        if (age <= 39) return AGE_35_39;
        if (age <= 44) return AGE_40_44;
        if (age <= 49) return AGE_45_49;
        return AGE_50_PLUS;
    }
}
