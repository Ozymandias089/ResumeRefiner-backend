package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class EmptyFileException extends BaseException {
    public EmptyFileException() {
        super(ErrorCode.EMPTY_FILE, "file must not be empty");
    }
}
