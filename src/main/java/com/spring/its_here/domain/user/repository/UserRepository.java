package com.spring.its_here.domain.user.repository;

import com.spring.its_here.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsernameAndHasDeletedFalse(String username);

    boolean existsByNicknameAndHasDeletedFalse(String nickname);

    Optional<UserEntity> findByUsername(String username);
}
