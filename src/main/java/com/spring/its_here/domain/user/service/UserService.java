package com.spring.its_here.domain.user.service;

import com.spring.its_here.domain.user.dto.request.UserSignupRequestDto;
import com.spring.its_here.domain.user.dto.request.UserLoginRequestDto;
import com.spring.its_here.domain.user.dto.response.TokenPairDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.dto.response.UserSelfGetResponseDto;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import com.spring.its_here.global.security.CustomUserDetails;
import com.spring.its_here.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    public UserResponseDto signup(UserSignupRequestDto userSignupRequestDto) {
        // 동일한 아이디, 닉네임 존재 확인
        if (userRepository.existsByUsernameAndHasDeletedFalse(userSignupRequestDto.username())
            && userRepository.existsByNicknameAndHasDeletedFalse(userSignupRequestDto.nickname())
        ) {
            throw new ItsHereException(ErrorCode.DUPLICATE_USERNAME);
        }

        // CUSTOMER, MANAGER 제외 생성 불가능
        if (userSignupRequestDto.role() != UserRole.CUSTOMER && userSignupRequestDto.role() != UserRole.OWNER) {
            throw new ItsHereException(ErrorCode.INVALID_REQUEST);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userSignupRequestDto.password());

        // 사용자 생성
        UserEntity user = UserEntity.create(
                userSignupRequestDto.username(),
                encodedPassword,
                userSignupRequestDto.nickname(),
                userSignupRequestDto.role()
        );

        // 사용자 저장
        userRepository.save(user);

        // 생성자 저장
        user.assignCreatedBy(user.getId());

        return new UserResponseDto(user.getId());
    }

    @Transactional
    public TokenPairDto login(UserLoginRequestDto userLoginRequestDto) {
        // 아이디와 비밀번호를 이용하여 인증 요청 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.username(),
                        userLoginRequestDto.password()
                );

        // AuthenticationManager를 통해 인증 수행
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        // 인증된 사용자 정보 가져옴
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // Access Token 생성
        String accessToken =
                jwtProvider.createAccessToken(userDetails);

        // Refresh Token 생성
        String refreshToken =
                jwtProvider.createRefreshToken(userDetails);

        return new TokenPairDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenPairDto reissue(String refreshToken) {
        // JWT 검증
        jwtProvider.validateToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);

        // 사용자 권한 조회
        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ItsHereException(ErrorCode.AUTH_UNAUTHORIZED)
                        );

        // 인증된 사용자 정보 가져옴
        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        // 새로운 AccessToken 생성
        String newAccessToken =
                jwtProvider.createAccessToken(userDetails);

        // 새로운 RefreshToken 생성
        String newRefreshToken =
                jwtProvider.createRefreshToken(userDetails);

        return new TokenPairDto(newAccessToken, newRefreshToken);
    }

    @Transactional(readOnly = true)
    public UserSelfGetResponseDto getSelf() {
        // SecurityContext에 저장된 현재 로그인 사용자의 PK를 조회
        Long userId = authenticationFacade.getCurrentUserId();

        // PK를 이용하여 최신 사용자 정보를 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ItsHereException(ErrorCode.USER_NOT_FOUND)
                );

        // 최신 사용자 정보를 응답 DTO로 변환하여 반환
        return new UserSelfGetResponseDto(
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );
    }
}
