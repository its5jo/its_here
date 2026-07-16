package com.spring.its_here.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class CookieUtil {

    // RefreshToken Cookie 생성
    public static Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        // JavaScript에서 접근 불가(XSS 방어)
        cookie.setHttpOnly(true);

        // HTTPS 환경에서만 전송
        // 현재는 개발 중이므로 false
        cookie.setSecure(false);

        // 모든 경로에서 Cookie 사용
        cookie.setPath("/");

        // 14일
        cookie.setMaxAge(60 * 60 * 24 * 14);

        return cookie;
    }

    // RefreshToken Cookie 조회
    public static String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
