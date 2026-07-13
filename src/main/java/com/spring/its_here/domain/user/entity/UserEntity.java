package com.spring.its_here.domain.user.entity;

import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.base.BaseDeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserEntity extends BaseDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "has_deleted", nullable = false)
    private Boolean hasDeleted;


    public static UserEntity create(String username, String password, String nickname, UserRole role) {
        return new UserEntity(
                null,
                username,
                password,
                nickname,
                role,
                false
        );
    }

    public void hasDeleted(Boolean hasDeleted) {
        this.hasDeleted = hasDeleted;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
