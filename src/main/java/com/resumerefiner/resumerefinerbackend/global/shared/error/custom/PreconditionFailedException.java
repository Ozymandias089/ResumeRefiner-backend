package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class PreconditionFailedException extends BaseException {
    // -1이면 "모름"
    private final long currentVersion;

    public PreconditionFailedException(String message, long currentVersion) {
        super(ErrorCode.PRECONDITION_FAILED, message);
        this.currentVersion = currentVersion;
    }
}
