package com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class DomainConflictException extends BaseException {
    public DomainConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}