package com.spring.its_here.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean hasHidden;

    @Column(nullable = false)
    private int price;

    private String imageUrl;

//    @JoinColumn(name = "store_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    private Store store;  // TODO: Store Entity 생성되면 주석 풀어서 연관관계 설정

    private Product(String name, String description, boolean hasHidden, int price, String imageUrl) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.description = description;
        this.hasHidden = hasHidden;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static Product create(
            String name,
            String description,
            boolean hasHidden,
            int price,
            String imageUrl
    ) {
        return new Product(
                name,
                description,
                hasHidden,
                price,
                imageUrl
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
