package com.spring.its_here.global.advice;


import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        String code,
        Map<String, Object> details,
        Instant timestamp
) {
    public static ErrorResponse from(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        return new ErrorResponse(
                message,
                errorCode == null ? null : errorCode.name(),
                details == null ? Map.of() : details,
                Instant.now()
        );
    }

    public static ErrorResponse fail(
            String message,
            String reason
    ) {
        return new ErrorResponse(
                message,
                "FAIL",
                Map.of("reason", reason),
                Instant.now()
        );
    }
}