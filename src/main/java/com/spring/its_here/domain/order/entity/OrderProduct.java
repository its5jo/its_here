package com.spring.its_here.domain.order.entity;

import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_order_product")
@Getter
@NoArgsConstructor
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public static OrderProduct createSnapshot(Order order, UUID productId,
                                              String productName, int productPrice,
                                              long quantity) {
        OrderProduct op = new OrderProduct();
        op.order = order;
        op.productId = productId;
        op.name = productName;
        op.price = productPrice;
        op.quantity = quantity;
        return op;
    }

}
