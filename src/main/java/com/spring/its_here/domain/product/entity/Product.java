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
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
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

    private Product(String name, String description, boolean hasHidden, Integer price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.hasHidden = hasHidden;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static Product create(Product product) {
        return new Product(
                product.getName(),
                product.getDescription(),
                product.isHasHidden(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    public void update(String name, String description, Boolean hasHidden, Integer price, String imageUrl) {
        if (name != null) {
            this.name = name;
        }

        if (description != null) {
            this.description = description;
        }

        if (hasHidden != null) {
            this.hasHidden = hasHidden;
        }

        if (price != null) {
            this.price = price;
        }

        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }

    }

}
