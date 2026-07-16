package com.spring.its_here.global.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.Instant;

@Getter
@MappedSuperclass
public class BaseDeletableEntity extends BaseUpdatableEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    public void delete(Long deletedBy) {
        if (isDeleted()) {
            throw new IllegalStateException("이미 삭제된 엔티티입니다.");
        }

        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }

    public void restore() {
        if (!isDeleted()) {
            throw new IllegalStateException("삭제되지 않은 엔티티입니다.");
        }

        this.deletedAt = null;
        this.deletedBy = null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
    
}
