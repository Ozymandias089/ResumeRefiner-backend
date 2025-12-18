package com.resumerefiner.resumerefinerbackend.member.domain.vo;

import com.resumerefiner.resumerefinerbackend.global.shared.domain.ValueObject;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Handle implements ValueObject {
    @Getter
    @Column(name = "handle", nullable = false, length = 32, unique = true)
    private String value;

    public Handle(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainRuleViolationException("handle must not be blank");
        }
        if (value.length() < 3 || value.length() > 32) {
            throw new DomainRuleViolationException("handle length must be between 3 and 32");
        }
        // 필요하면 정규식 검증 추가 (알파벳/숫자/언더스코어만 허용 등)
        this.value = value;
    }

    public static Handle of(String value) {
        return new Handle(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
