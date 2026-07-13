package com.spring.its_here.domain.address.entity;

import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address extends BaseDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public static Address create(String address, UserEntity user) {
        Address addressEntity = new Address();
        addressEntity.address = address;
        addressEntity.user = user;

        return addressEntity;
    }
}
