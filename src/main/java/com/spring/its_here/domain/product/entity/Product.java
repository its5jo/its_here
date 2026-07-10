package com.spring.its_here.domain.product.entity;

import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "has_hidden", nullable = false)
    private boolean hasHidden;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "image_url")
    private String imageUrl;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @JoinColumn(name = "store_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Store store;  // TODO: Store Entity 생성되면 주석 풀어서 연관관계 설정

    private Product(String name, String description, boolean hasHidden, int price, String imageUrl, Store store) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.description = description;
        this.hasHidden = hasHidden;
        this.price = price;
        this.imageUrl = imageUrl;
        this.store = store;
    }

    public static Product create(
            String name,
            String description,
            boolean hasHidden,
            int price,
            String imageUrl,
            Store store
    ) {
        return new Product(
                name,
                description,
                hasHidden,
                price,
                imageUrl,
                store
        );
    }

    public void update(
            String name,
            String description,
            Boolean hasHidden,
            Integer price,
            String imageUrl
    ) {
        if (name != null) {
            validateName(name);
            this.name = name;
        }

        if (description != null) {
            this.description = description;
        }

        if (hasHidden != null) {
            this.hasHidden = hasHidden;
        }

        if (price != null) {
            validatePrice(price);
            this.price = price;
        }

        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }

    public void delete(Long deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }


    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
    }

    private static void validatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다.");
        }
    }


}
