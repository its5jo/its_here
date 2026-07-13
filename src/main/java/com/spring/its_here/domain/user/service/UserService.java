package com.spring.its_here.domain.user.service;

import com.spring.its_here.domain.user.dto.request.UserSignupRequestDto;
import com.spring.its_here.domain.user.dto.request.UserLoginRequestDto;
import com.spring.its_here.domain.user.dto.request.UserUpdateRequestDto;
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

    // 동일 아이디 존재 확인 로직
    private void existsByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ItsHereException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    // 인증된 user 객체 조회 및 반환
    private UserEntity getCurrentUser() {
        // SecurityContext에 저장된 현재 로그인 사용자의 PK를 조회
        Long userId = authenticationFacade.getCurrentUserId();

        // PK를 이용하여 최신 사용자 정보를 조회
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ItsHereException(ErrorCode.USER_NOT_FOUND)
                );
    }

    @Transactional
    public UserResponseDto signup(UserSignupRequestDto userSignupRequestDto) {
        // 동일한 아이디 존재 확인
        existsByUsername(userSignupRequestDto.username());

        // CUSTOMER, OWNER만 회원가입 할 수 있음
        if (userSignupRequestDto.role() != UserRole.CUSTOMER
                && userSignupRequestDto.role() != UserRole.OWNER) {
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
        // 현재 인증된 사용자
        UserEntity user = getCurrentUser();

        // 삭제된 사용자일 경우
        if (user.getHasDeleted()) {
            throw new ItsHereException(ErrorCode.USER_NOT_FOUND);
        }

        // 최신 사용자 정보를 응답 DTO로 변환하여 반환
        return new UserSelfGetResponseDto(
                user.getUsername(),
                user.getNickname(),
                user.getRole()
        );
    }

    @Transactional
    public void delete(Long userId) {
        // 현재 인증된 사용자
        UserEntity currentUser = getCurrentUser();

        // 다른 사용자를 삭제 요청한 경우
        if (!currentUser.getId().equals(userId)) {
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        // 이미 삭제된 사용자일 경우
        if (currentUser.getHasDeleted()) {
            throw new ItsHereException(ErrorCode.USER_NOT_FOUND);
        }

        // Soft Delete 진행 및 감사 필드 작성
        currentUser.delete(userId);
        currentUser.hasDeleted(true);
    }

    @Transactional
    public UserResponseDto update(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        // 현재 인증된 사용자
        UserEntity currentUser = getCurrentUser();

        // 다른 유저의 정보 수정을 요청할 경우
        if (!currentUser.getId().equals(userId)) {
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        // 비밀번호와 활동명 변경
        currentUser.updatePassword(passwordEncoder.encode(userUpdateRequestDto.password()));
        currentUser.updateNickname(userUpdateRequestDto.nickname());

        return new UserResponseDto(userId);
    }
}
