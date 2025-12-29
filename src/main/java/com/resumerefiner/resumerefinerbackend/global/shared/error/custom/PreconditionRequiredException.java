package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class PreconditionRequiredException extends BaseException {
    public PreconditionRequiredException(String message) {
        super(ErrorCode.PRECONDITION_REQUIRED, message);
    }
}
