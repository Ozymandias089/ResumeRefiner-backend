package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class DuplicateHandleException extends BaseException {
    public DuplicateHandleException() {
        super(ErrorCode.DUPLICATE_HANDLE, "Handle already in use");
    }
}