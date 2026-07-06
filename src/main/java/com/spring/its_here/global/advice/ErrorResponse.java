package com.spring.its_here.global.advice;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details
) {
    public static ErrorResponse from(
            Instant timestamp,
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        return new ErrorResponse(
                timestamp,
                errorCode == null ? null : errorCode.name(),
                message,
                details == null ? Map.of() : details
        );
    }
}