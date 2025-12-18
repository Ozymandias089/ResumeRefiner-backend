package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class InternalServerException extends BaseException {
    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_ERROR, message);
    }
}
