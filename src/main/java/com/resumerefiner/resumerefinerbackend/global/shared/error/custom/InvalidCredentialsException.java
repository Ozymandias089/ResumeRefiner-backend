package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }
}
