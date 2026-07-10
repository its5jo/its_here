package com.spring.its_here.global.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 올바르지 않습니다.", "U-001"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력조건을 불충족하였습니다.", "U-002"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "동일 아이디가 존재합니다.", "U-003"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "U-004"),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "동일한 카테고리가 존재합니다.", "C-001"),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점", "U-001"),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "리뷰내용 255자 이하", "R-002"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다.", "O-001"),
    REVIEW_FORBIDDEN(HttpStatus.NOT_FOUND, "리뷰작성 권한이 없습니다.", "R-001")
    ;

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
