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
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다.", "CM-007"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.", "CM-008"),

    // auth & user
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 올바르지 않습니다.", "U-001"),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "U-005"), // TODO: 컨벤션 정립후 code 위치 또는 code 번호 수정
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력조건을 불충족하였습니다.", "U-002"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "동일 아이디가 존재합니다.", "U-003"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "U-004"),

    // category
    CATEGORY_NAME_DUPLICATE(HttpStatus.CONFLICT, "동일한 카테고리가 존재합니다.", "C-001"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다.", "C-002"),
    CATEGORY_INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "카테고리 이름과 생성일시로만 정렬할 수 있습니다.", "C-003"),
    CATEGORY_HIDDEN(HttpStatus.BAD_REQUEST, "숨김 처리된 카테고리입니다.", "C-004"),

    // area
    AREA_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 지역입니다.", "A-001"),
    AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다.", "A-002"),
    AREA_INVALID_SORT_BY(HttpStatus.BAD_REQUEST, "정렬 기준은 createdAt만 가능합니다.", "A-003"),
    AREA_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 서비스 지역입니다.", "A-004"),

    // order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다.", "O-001"),
    ORDER_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 주문 내역에 대한 접근 권한이 없습니다.", "O-002"),
    ORDER_CANCEL_TIMEOUT(HttpStatus.BAD_REQUEST, " 취소 가능한 주문 상태가 아닙니다.", "O-003"),
    ORDER_STATUS_TRANSITION_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태 변경입니다.", "O-004"),

    // payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다.", "PM-001"),

    // store
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "가게가 존재하지 않습니다.", "S-001"),
    STORE_NAME_DUPLICATE(HttpStatus.CONFLICT, "동일한 이름의 가게가 존재합니다.", "S-002"),
    STORE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 등록한 가게가 존재합니다.", "S-003"),
    STORE_NOT_OWNED(HttpStatus.FORBIDDEN, "가게 접근 권한이 없습니다.", "S-004"),
    STORE_CLOSED(HttpStatus.BAD_REQUEST, "가게가 영업 중이 아닙니다.", "S-005"),
    STORE_AREA_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "주문 가능한 지역의 음식점이 아닙니다.", "S-006"),

    // review
    REVIEW_CONTENT_VALID(HttpStatus.BAD_REQUEST, "리뷰내용 255자 이하입니다", "R-001"),
    REVIEW_ORDER_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "완료된 주문에만 리뷰를 작성할 수 있습니다.", "R-002"),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 리뷰입니다.", "R-003"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다.", "R-004"),
    REVIEW_INVALID_SORT_BY(HttpStatus.BAD_REQUEST, "정렬 기준은 createdAt만 가능합니다.", "R-005"),
    REVIEW_UPDATE_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "리뷰 수정 가능 기간이 만료되었습니다.", "R-006"),
    REVIEW_CONFLICT(HttpStatus.CONFLICT, "리뷰가 다른 요청에 의해 변경되었습니다. 다시 시도해주세요.", "R-007"),

    // product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다.", "P-001"),

    // address
    ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 주소입니다.", "AD-001"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다.", "AD-002"),

    // ai infrastructure
    AI_API_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "ai api 요청이 실패하였습니다.", "AI-001"),

    // ai_history
    AI_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ai기록을 찾을 수 없습니다.", "AH-001")
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
