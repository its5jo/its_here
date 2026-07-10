package com.spring.its_here.domain.category.entity;

import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "has_hidden", nullable = false)
    private boolean hasHidden;

    @Column(name = "has_deleted", nullable = false)
    private boolean hasDeleted;

    public Category(String name, boolean hasHidden) {
        this.name = name;
        this.hasHidden = hasHidden;
        this.hasDeleted = false;
    }
}
