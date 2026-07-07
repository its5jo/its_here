package com.spring.its_here.domain.user.entity;

import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.BaseEntity;
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

    private String username;

    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

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
