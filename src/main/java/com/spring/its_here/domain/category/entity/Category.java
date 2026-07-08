package com.spring.its_here.domain.category.entity;

import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_category")
//AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean hasHidden;

    @Column(nullable = false)
    private boolean hasDeleted;

    public Category(String name, boolean hasHidden) {
        this.name = name;
        this.hasHidden = hasHidden;
        this.hasDeleted = false;
    }
}
