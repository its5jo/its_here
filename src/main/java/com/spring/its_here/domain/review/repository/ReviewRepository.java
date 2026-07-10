package com.spring.its_here.domain.review.repository;

import com.spring.its_here.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrder_Id(UUID orderId);
}
