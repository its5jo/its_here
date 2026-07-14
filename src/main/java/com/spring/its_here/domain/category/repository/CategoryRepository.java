package com.spring.its_here.domain.category.repository;

import com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByNameAndDeletedAtIsNull(String name);

    Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId);

    @Query("""
        SELECT new com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto(
                        c.id,
                        c.name,
                        c.hasHidden
        
                    )
        FROM Category c
        WHERE c.deletedAt IS NULL
          AND (:name IS NULL OR c.name LIKE %:name%)
          AND (:hasHidden IS NULL OR c.hasHidden = :hasHidden)
        """)
    Page<CategoryGetAllResponseDto> getAllCategories(
            @Param("name") String name,
            @Param("hasHidden") Boolean hasHidden,
            Pageable pageable
    );
}
