package com.spring.its_here.domain.order.repository;

import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByIdAndDeletedAtIsNull(UUID orderId);

    Page<Order> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatusAndDeletedAtIsNull(Long userId, OrderStatus status, Pageable pageable);

    // OWNER - 가게 별 주문 내역 조회
    Page<Order> findByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Page<Order> findByDeletedAtIsNull(Pageable pageable);
}
