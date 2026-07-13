package com.spring.its_here.domain.store.repository;

import com.spring.its_here.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

    boolean existsByNameAndDeletedAtIsNull(String name);
    boolean existsByUserIdAndDeletedAtIsNull(Long userId);
    boolean existsByNameAndDeletedAtIsNullAndIdNot(String name, UUID storeId);

    Optional<Store> findByIdAndDeletedAtIsNull(UUID storeId);

    @Modifying
    @Query("""
        update Store s
           set s.reviewTotalRating = s.reviewTotalRating + :rating,
               s.reviewTotalCount = s.reviewTotalCount + 1
         where s.id = :storeId
    """)
    void addReview(UUID storeId, Double rating);

    @Modifying
    @Query("""
        update Store s
           set s.reviewTotalRating = s.reviewTotalRating - :oldRating + :newRating
         where s.id = :storeId
    """)
    void modifyReviewRating(UUID storeId, Double oldRating, Double newRating);

    @Modifying
    @Query("""
        update Store s
           set s.reviewTotalRating = s.reviewTotalRating - :rating,
               s.reviewTotalCount = s.reviewTotalCount - 1
         where s.id = :storeId
    """)
    void deleteReview(UUID storeId, Double rating);

}
