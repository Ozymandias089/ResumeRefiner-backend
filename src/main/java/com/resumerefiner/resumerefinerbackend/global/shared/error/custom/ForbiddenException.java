package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN, "Access denied");
    }
}