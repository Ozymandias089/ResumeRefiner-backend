package com.resumerefiner.resumerefinerbackend.global.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBase(BaseException ex, HttpServletRequest req) {
        var status = ex.errorCode().status();
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        ex.errorCode().name(),
                        ex.getMessage(),
                        req.getRequestURI(),
                        ex.fieldErrors(),
                        ex.details()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<BaseException.FieldError> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toField)
                .toList();

        var code = ErrorCode.VALIDATION_FAILED;
        var status = code.status();

        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        code.name(),
                        "Validation failed",
                        req.getRequestURI(),
                        fields,
                        Map.of()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex, HttpServletRequest req) {
        var code = ErrorCode.INTERNAL_ERROR;
        var status = code.status();

        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        code.name(),
                        "Unexpected error",
                        req.getRequestURI(),
                        List.of(),
                        Map.of()
                )
        );
    }

    private BaseException.FieldError toField(FieldError fe) {
        return new BaseException.FieldError(fe.getField(), fe.getDefaultMessage());
    }
}

