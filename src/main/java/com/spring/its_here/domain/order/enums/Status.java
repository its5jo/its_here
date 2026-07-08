package com.spring.its_here.domain.order.enums;

public enum Status {
    REQUESTED,  // 주문 요청 (CUSTOMER가 주문 생성 시 자동으로 설정)
    ACCEPTED,  // 주문 수락 (OWNER)
    COOKED,  // 조리 완료 (OWNER)
    DELEVERING,  // 배송수령/배달중(OWNER)
    DELEVERED,  // 배송 완료 (OWNER)
    COMPLETED,  // 주문 완료(OWNER)
    CANCELED  // 취소
}
