package com.spring.its_here.domain.store.entity;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Table(name = "p_store")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Store extends BaseDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "has_open", nullable = false)
    private Boolean hasOpen;

    @Column(name = "open_at")
    private LocalTime openAt;

    @Column(name = "closed_at")
    private LocalTime closedAt;

    @Column(name = "review_total_rating", nullable = false)
    private Double reviewTotalRating;

    @Column(name = "review_total_count" ,nullable = false)
    private Long reviewTotalCount;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    public static Store createStore(
            String name, String address,
            UserEntity user, Category category, Area area,
            boolean hasOpen, LocalTime openAt, LocalTime closedAt
    ) {
        Store store = new Store();

        store.name = name;
        store.address = address;
        store.user = user;
        store.category = category;
        store.area = area;
        store.hasOpen = hasOpen;
        store.openAt = openAt;
        store.closedAt = closedAt;
        store.reviewTotalRating = 0D;
        store.reviewTotalCount = 0L;

        return store;
    }

    public void accumulateReview(double rating) {
        this.reviewTotalRating += rating;
        this.reviewTotalCount += 1;
    }

    // 수정 메서드

    // 삭제 메서드

}