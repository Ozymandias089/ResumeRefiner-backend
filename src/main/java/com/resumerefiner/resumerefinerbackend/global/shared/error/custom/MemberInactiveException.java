package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

public class MemberInactiveException extends BaseException {
    public MemberInactiveException(String message) {
        super(ErrorCode.MEMBER_INACTIVE, message);
    }
}
