package com.resumerefiner.resumerefinerbackend.global.shared.error;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<BaseException.FieldError> fieldErrors,
        Map<String, Object> details
) {}