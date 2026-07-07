package com.spring.its_here.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    // SecretKey 객체로 변환
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
    }

    // Access Token 생성
    public String createAccessToken(CustomUserDetails userDetails) {
        Date now = new Date();

        Date expiration =
                new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                // 로그인 아이디 저장
                .subject(userDetails.getUsername())
                // 사용자 PK 저장
                .claim("userId", userDetails.getUserId())
                // 사용자 권한 저장
                .claim(
                        "role",
                        userDetails.getRole().name()
                )
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(CustomUserDetails userDetails) {
        Date now = new Date();

        Date expiration =
                new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                // 로그인 식별자
                .subject(userDetails.getUsername())
                // 사용자 PK
                .claim("userId", userDetails.getUserId())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 내부 Claim 정보 반환
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    // 만료 시간 반환
    public LocalDateTime getRefreshTokenExpiredAt() {
        return LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);
    }

    // JWT에 저장된 username 반환
    public String getUsername(String token) {
        return getClaims(token).getSubject();

    }

    // JWT에 저장된 사용자 PK 반환
    public Long getUserId(String token) {
        Object userId = getClaims(token).get("userId");

        return ((Number) userId).longValue();
    }

    // JWT 만료 시간 반환
    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }
}
