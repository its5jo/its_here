package com.spring.its_here.domain.area.entity;


import com.spring.its_here.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_area")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Area extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "town", nullable = false)
    private String town;

    @Column(name = "has_available", nullable = false)
    private boolean hasAvailable;

    @Column(name = "has_deleted", nullable = false)
    private boolean hasDeleted = false;
//
//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private Instant updateAt;
//
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    private Long updatedBy;
//
//    @Column(name = "deleted_at")
//    private Instant deletedAt;
//
//    @Column(name = "deleted_by")
//    private Long deletedBy;

//    public void delete(Long deletedBy) {
//        this.deletedAt = Instant.now();
//        this.deletedBy = deletedBy;
//        this.hasDeleted = true;
//    }
}
