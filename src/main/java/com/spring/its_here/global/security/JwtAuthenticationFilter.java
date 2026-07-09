package com.spring.its_here.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization Header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header가 존재하지 않거나 Bearer 형식이 아니라면 JWT 인증 수행하지 않음
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 제거
        String token = authorizationHeader.substring(7);

        // JWT 검증
        if (!jwtProvider.validateToken(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        // JWT에서 PK 추출
        Long userId = jwtProvider.getUserId(token);

        // PK를 이용해서 사용자 조회
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // Authentication 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        // 요청 정보 저장
        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );


        // SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);


        // 다음 Filter 실행
        filterChain.doFilter(request, response);
    }
}
