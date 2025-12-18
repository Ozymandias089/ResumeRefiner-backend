package com.resumerefiner.resumerefinerbackend.global.shared.error.custom;

import com.resumerefiner.resumerefinerbackend.global.shared.error.BaseException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.ErrorCode;

import java.util.Map;
import java.util.Set;

public class UnsupportedMediaTypeException extends BaseException {
    public UnsupportedMediaTypeException(String contentType, Set<String> allowed) {
        super(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "unsupported content type: " + contentType,
                null,
                Map.of(
                        "contentType", contentType,
                        "allowed", allowed
                )
        );
    }
}