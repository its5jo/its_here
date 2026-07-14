package com.spring.its_here.domain.aihistory.entity;

import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_ai_history")
public class AiHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "prompt", nullable = false)
    private String prompt;

    @Column(name = "response", nullable = false)
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private AiHistory(Product product, String prompt, String response) {
        this.product = product;
        this.prompt = prompt;
        this.response = response;
    }

    public static AiHistory create(Product product, String prompt, String response) {
        return new AiHistory(product, prompt, response);
    }

}
