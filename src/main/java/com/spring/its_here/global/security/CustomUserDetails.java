package com.spring.its_here.global.security;

import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    // 사용자가 가지고 있는 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    // 로그인 시 사용자의 암호화된 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 로그인 시 사용자의 아이디 반환
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 로그인한 사용자의 PK 반환
    public Long getUserId() {
        return user.getId();
    }

    // 로그인 사용자의 객체 반환
    public UserEntity getUserEntity() {
        return user;
    }

    // 로그인한 사용자의 Role 반환
    public UserRole getRole() {
        return user.getRole();
    }
}
