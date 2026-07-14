package com.spring.its_here.domain.order.enums;

public enum OrderStatus {
    REQUESTED,  // 주문 요청 (CUSTOMER가 주문 생성 시 자동으로 설정)
    ACCEPTED,  // 주문 수락 (OWNER)
    COOKED,  // 조리 완료 (OWNER)
    DELIVERING,  // 배송수령/배달중(OWNER)
    COMPLETED,  // 배송 완료 및 주문 완료(OWNER)
    CANCELED; // 취소

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case REQUESTED -> next == ACCEPTED || next == CANCELED;
            case ACCEPTED -> next == COOKED;
            case COOKED -> next == DELIVERING;
            case DELIVERING -> next == COMPLETED;
            case COMPLETED, CANCELED -> false;
        };
    }
}
