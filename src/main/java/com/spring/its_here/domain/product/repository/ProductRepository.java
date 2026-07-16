package com.spring.its_here.domain.product.repository;

import com.spring.its_here.domain.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByStoreId(UUID storeId);

    @Query("""
                SELECT p
                FROM Product p
                WHERE p.store.id = :storeId
                  AND p.deletedAt IS NULL
                  AND p.hasHidden = false
                  AND (
                      :isFirstPage = true
                      OR (
                          :sortDirection = 'ASCENDING'
                          AND (
                              p.createdAt > :cursor
                              OR (
                                  p.createdAt = :cursor
                                  AND p.id > :idAfter
                              )
                          )
                      )
                      OR (
                          :sortDirection = 'DESCENDING'
                          AND (
                              p.createdAt < :cursor
                              OR (
                                  p.createdAt = :cursor
                                  AND p.id < :idAfter
                              )
                          )
                      )
                  )
            """)
    Slice<Product> searchProductsByCursor(
            @Param("storeId") UUID storeId,
            @Param("cursor") Instant cursor,
            @Param("idAfter") UUID idAfter,
            @Param("sortDirection") String sortDirection,
            @Param("isFirstPage") boolean isFirstPage,
            Pageable pageable
    );
}
