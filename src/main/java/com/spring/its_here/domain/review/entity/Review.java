package com.spring.its_here.domain.review.entity;


import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "content")
    private String content;

    // 임시
    private UUID orderId;
    private UUID storeId;
    private UUID userId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    public static Review create(
            double rating,
            String content
//            UUID orderId,
//            UUID storeId,
//            UUID userId
    ) {
        Review review = new Review();
        review.rating = rating;
        review.content = content;
//        review.orderId = orderId;
//        review.storeId = storeId;
//        review.userId = userId;
        return review;
    }
}
