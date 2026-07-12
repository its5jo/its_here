package com.spring.its_here.domain.review.repository;

import com.spring.its_here.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByOrderId(UUID orderId);

    Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);
}
