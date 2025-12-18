package com.resumerefiner.resumerefinerbackend.global.shared.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),
    // 400
    EMPTY_FILE(HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    // 401/403
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    MEMBER_INACTIVE(HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND),
    FORBIDDEN(HttpStatus.FORBIDDEN),

    // 404
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),

    // 409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT),
    DUPLICATE_HANDLE(HttpStatus.CONFLICT),
    CONFLICT(HttpStatus.CONFLICT),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
