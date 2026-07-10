package com.spring.its_here.domain.order.entity;

import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.global.base.BaseDeletableEntity;
import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor
public class Order extends BaseDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "request_memo")
    private String requestMemo;

    public static Order create(UUID storeId, Long userId,
                               String deliveryAddress, String requestMemo,
                               int totalAmount) {
        Order order = new Order();
        order.storeId = storeId;
        order.userId = userId;
        order.deliveryAddress = deliveryAddress;
        order.requestMemo = requestMemo;
        order.totalAmount = totalAmount;
        order.status = OrderStatus.REQUESTED;
        return order;
    }
}
