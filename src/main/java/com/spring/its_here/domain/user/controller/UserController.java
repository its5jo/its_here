package com.spring.its_here.domain.user.controller;

import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
import com.spring.its_here.domain.user.dto.request.UserLoginRequestDto;
import com.spring.its_here.domain.user.dto.response.TokenPairDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.dto.response.UserSelfGetResponseDto;
import com.spring.its_here.domain.user.dto.response.UserTokenResponseDto;
import com.spring.its_here.domain.user.service.UserService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 관련 API 입니다.")
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "ID와 PW, nickname과 role을 이용하여 회원가입합니다."
    )
    @SecurityRequirements()
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "회원가입 성공",
                        userService.signup(userCreateRequestDto)
                ));
    }

    @Operation(
            summary = "로그인",
            description = "ID와 PW를 이용하여 Access Token, Refresh Token을 발급합니다."
    )
    @SecurityRequirements()
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
                        new UserTokenResponseDto(
                                tokenPairDto.accessToken()
                        )
                ));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token을 통해 Access Token과 Refresh Token을 재발급합니다."
    )
    @SecurityRequirements()
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
                        new UserTokenResponseDto(
                                tokenPairDto.accessToken()
                        )
                )
        );
    }

    @Operation(
            summary = "내 정보 조회",
            description = "권한이 있는 사용자가 자신의 정보를 조회합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSelfGetResponseDto>> getCurrentUser() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "내 정보 조회 성공",
                        userService.getSelf()
                ));
    }
}
