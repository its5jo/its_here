package com.spring.its_here.domain.review.entity;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
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

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "has_deleted", nullable = false)
    private boolean hasDeleted = false;

    // 임시
    private UUID storeId;
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    public static Review create(
            double rating,
            String content,
            Order order
//            Store storeId,
//            User userId
    ) {
        Review review = new Review();
        review.rating = rating;
        review.content = content;
        review.order = order;
//        review.storeId = storeId;
//        review.userId = userId;
        return review;
    }

    public void delete(Long deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
        this.hasDeleted = true;
    }
}
