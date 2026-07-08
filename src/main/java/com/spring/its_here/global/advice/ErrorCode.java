package com.spring.its_here.global.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 올바르지 않습니다.", "U-001"),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점", "U-001"),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "리뷰내용 255자 이하", "R-002");

    private final String message;
    private final HttpStatus status;
    private final String code;

    ErrorCode(
            HttpStatus status,
            String message,
            String code
    ) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
