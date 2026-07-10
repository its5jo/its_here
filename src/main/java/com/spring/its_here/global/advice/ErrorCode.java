package com.spring.its_here.global.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {
    // common
//    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다.", "CM-001"), // TODO: errorCode 컨벤션 정립후 추가
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", "CM-002"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다.", "CM-003"),
    INVALID_STATE(HttpStatus.CONFLICT, "요청을 처리할 수 없는 상태입니다.", "CM-004"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다.", "CM-005"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다.", "CM-006"),

    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 올바르지 않습니다.", "U-001"),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "U-005"), // TODO: 컨벤션 정립후 code 위치 또는 code 번호 수정
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력조건을 불충족하였습니다.", "U-002"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "동일 아이디가 존재합니다.", "U-003"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "U-004"),

    CATEGORY_NAME_DUPLICATE(HttpStatus.CONFLICT, "동일한 카테고리가 존재합니다.", "C-001"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다.", "C-002"),

    AREA_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 지역입니다.", "A-001"),
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다.", "A-001"),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "가게가 존재하지 않습니다.", "S-001"),
    STORE_NAME_DUPLICATE(HttpStatus.CONFLICT, "동일한 이름의 가게가 존재합니다.", "S-002"),
    STORE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록한 가게가 존재합니다.", "S-003");

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
