package com.spring.its_here.domain.auth.repository;

import com.spring.its_here.domain.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Optional<RefreshTokenEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
