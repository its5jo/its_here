package com.spring.its_here.domain.review.entity;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "content")
    private String content;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public static Review create(
            double rating,
            String content,
            Order order,
//            Store store,
            UserEntity user
    ) {
        Review review = new Review();
        review.rating = rating;
        review.content = content;
        review.order = order;
//        review.store = store;
        review.user = user;
        return review;
    }
}
