package com.spring.its_here.global.security;

import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    // 현재 로그인한 사용자의 정보를 반환
    public CustomUserDetails getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ItsHereException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    // 현재 로그인한 사용자의 PK 반환
    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
}
