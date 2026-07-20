package com.spring.its_here.domain.aihistory.repository;

import com.spring.its_here.domain.aihistory.entity.AiHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AiHistoryRepository extends JpaRepository<AiHistory, UUID> {

    @Query("""
            SELECT ah
            FROM AiHistory ah
            JOIN FETCH ah.product
            """)
    List<AiHistory> findAllWithProduct();


    @Query("""
            SELECT ah
            FROM AiHistory ah
            WHERE ah.product.id = :productId
              AND (
                  :isFirstPage = true
                  OR (
                      :sortDirection = 'ASCENDING'
                      AND (
                          ah.createdAt > :cursor
                          OR (
                              ah.createdAt = :cursor
                              AND ah.id > :idAfter
                          )
                      )
                  )
                  OR (
                      :sortDirection = 'DESCENDING'
                      AND (
                          ah.createdAt < :cursor
                          OR (
                              ah.createdAt = :cursor
                              AND ah.id < :idAfter
                          )
                      )
                  )
              )
            """)
    Slice<AiHistory> searchAiHistoriesByCursor(
            @Param("productId") UUID productId,
            @Param("cursor") Instant cursor,
            @Param("idAfter") UUID idAfter,
            @Param("sortDirection") String sortDirection,
            @Param("isFirstPage") boolean isFirstPage,
            Pageable pageable
    );
}
