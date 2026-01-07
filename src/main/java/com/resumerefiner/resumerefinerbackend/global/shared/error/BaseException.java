package com.resumerefiner.resumerefinerbackend.global.shared.error;

import java.util.List;
import java.util.Map;

public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<FieldError> fieldErrors;
    private final Map<String, Object> details;

    protected BaseException(ErrorCode errorCode, String message) {
        this(errorCode, message, List.of(), Map.of());
    }

    protected BaseException(ErrorCode errorCode, String message,
                            List<FieldError> fieldErrors,
                            Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors == null ? List.of() : fieldErrors;
        this.details = details == null ? Map.of() : details;
    }

    public ErrorCode errorCode() { return errorCode; }
    public List<FieldError> fieldErrors() { return fieldErrors; }
    public Map<String, Object> details() { return details; }

    public record FieldError(String field, String message) {}
}