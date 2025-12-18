package com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class DomainRuleViolationException extends BaseException {
    public DomainRuleViolationException(String message) {
        super(ErrorCode.INVALID_ARGUMENT, message);
    }

    public static DomainRuleViolationException of(String field, String message) {
        return new DomainRuleViolationException(message);
        // fieldErrors까지 쓰고 싶으면 BaseException 확장 설계에 맞춰 추가
    }
}
