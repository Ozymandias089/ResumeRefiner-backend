package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL, "Email already in use");
    }
}