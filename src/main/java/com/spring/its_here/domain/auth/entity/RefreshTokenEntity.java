package com.spring.its_here.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user pk
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    public RefreshTokenEntity(
            Long userId,
            String refreshToken,
            LocalDateTime expiredAt
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
        this.createdAt = LocalDateTime.now();
    }


    public void updateToken(
            String refreshToken,
            LocalDateTime expiredAt
    ) {
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }
}
