package com.spring.its_here.domain.review.entity;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Version 덕분에 Hibernate가 이 필드를 동시성 검사 전용 컬럼으로 취급해서 자동으로 UPDATE를 날리는 것
    @Version
    @Column(nullable = false)
    private Long version;

    public static Review savedReview(
            double rating,
            String content,
            UserEntity user,
            Store store,
            Order order
    ) {
        Review review = new Review();
        review.rating = rating;
        review.content = content;
        review.user = user;
        review.store = store;
        review.order = order;
        return review;
    }

    public void updateReview(
            double rating,
            String content
    ) {
        this.rating = rating;
        this.content = content;
    }
}
