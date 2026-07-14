package com.spring.its_here.domain.review.repository;

import com.spring.its_here.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByOrderId(UUID orderId);

    @Query("""
            SELECT r
            FROM Review r
            WHERE r.deletedAt IS NULL
            AND (:storeId IS NULL OR r.store.id = :storeId)
            AND (:rating IS NULL OR r.rating = :rating)
            """)
    Page<Review> searchReviews(
            @Param("storeId") UUID storeId,
            @Param("rating") Double rating,
            Pageable pageable
    );

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);
}
