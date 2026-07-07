package com.spring.its_here.domain.user.controller;

import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
import com.spring.its_here.domain.user.dto.request.UserLoginRequestDto;
import com.spring.its_here.domain.user.dto.response.TokenPairDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.dto.response.UserTokenResponseDto;
import com.spring.its_here.domain.user.service.UserService;
import com.spring.its_here.global.ApiResponse;
import com.spring.its_here.global.security.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "회원가입 성공",
                        "SUCCESS",
                        userService.signup(userCreateRequestDto)
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserTokenResponseDto>> login(
            @Valid @RequestBody UserLoginRequestDto userLoginRequestDto,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPairDto = userService.login(userLoginRequestDto);

        // Refresh Token Cookie 생성
        Cookie refreshTokenCookie =
                CookieUtil.createRefreshTokenCookie(
                        tokenPairDto.refreshToken()
                );

        // Response에 Cookie 추가
        response.addCookie(refreshTokenCookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "로그인 성공",
                        "SUCCESS",
                        new UserTokenResponseDto(
                                tokenPairDto.accessToken()
                        )
                ));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<UserTokenResponseDto>> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // Cookie에서 RefreshToken 추출
        String refreshToken =
                CookieUtil.getRefreshToken(request);

        // 재발급
        TokenPairDto tokenPairDto =
                userService.reissue(refreshToken);

        // 새 RefreshToken Cookie 저장
        response.addCookie(
                CookieUtil.createRefreshTokenCookie(
                        tokenPairDto.refreshToken()
                )
        );

        // 새 AccessToken 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "재발급 성공",
                        "SUCCESS",
                        new UserTokenResponseDto(
                                tokenPairDto.accessToken()
                        )
                )
        );
    }
}
