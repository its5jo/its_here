package com.spring.its_here.global.advice;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        String code,
        Map<String, Object> details,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    public static ErrorResponse from(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        return new ErrorResponse(
                message,
               "FAIL",
                details == null ? Map.of() : details,
                LocalDateTime.now()
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
                LocalDateTime.now()
        );
    }
}