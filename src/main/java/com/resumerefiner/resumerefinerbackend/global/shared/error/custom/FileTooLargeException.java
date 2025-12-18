package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

import java.util.Map;

public class FileTooLargeException extends BaseException {
    public FileTooLargeException(long maxBytes) {
        super(
                ErrorCode.FILE_TOO_LARGE,
                "file size must be <= " + maxBytes + " bytes",
                null,
                Map.of("maxBytes", maxBytes)
        );
    }
}