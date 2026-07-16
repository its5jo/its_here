package com.spring.its_here.domain.category.entity;

import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "has_hidden", nullable = false)
    private boolean hasHidden;

    public static Category createCategory(
            String name, boolean hasHidden
    ) {
        Category category = new Category();

        category.name = name;
        category.hasHidden = hasHidden;

        return category;
    }

    public void updateCategory(String name, Boolean hasHidden){
        this.name = name;
        this.hasHidden = hasHidden;
    }

}
