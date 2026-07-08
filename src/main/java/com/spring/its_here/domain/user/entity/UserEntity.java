package com.spring.its_here.domain.user.entity;

import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.base.BaseEntity;
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
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
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
}
