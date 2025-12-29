package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.Gender;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProfileViewDTO(
        String name,
        Gender gender,
        String email,
        String phone,
        String location,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        Integer age // 만 나이 (계산 결과, null 허용)
) {
}
