package com.spring.its_here.domain.user.service;

import com.spring.its_here.domain.auth.entity.RefreshTokenEntity;
import com.spring.its_here.domain.auth.repository.RefreshTokenRepository;
import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원가입 API 테스트")
    class SignUpTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup_success() {
            // given
            UserCreateRequestDto request = new UserCreateRequestDto(
                    "testUser",
                    "Password123!",
                    "테스터",
                    UserRole.CUSTOMER
            );

            when(userRepository.existsByUsernameAndHasDeletedFalse("testUser"))
                    .thenReturn(false);

            when(passwordEncoder.encode("Password123!"))
                    .thenReturn("encodedPassword");

            when(userRepository.save(any(UserEntity.class)))
                    .thenAnswer(invocation -> {
                        UserEntity user = invocation.getArgument(0);

                        ReflectionTestUtils.setField(user, "id", 1L);

                        return user;
                    });

            // when
            UserResponseDto response = userService.signup(request);

            // then
            assertThat(response.userId()).isEqualTo(1L);

            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(captor.capture());

            UserEntity savedUser = captor.getValue();

            assertThat(savedUser.getUsername()).isEqualTo("testUser");
            assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
            assertThat(savedUser.getNickname()).isEqualTo("테스터");
            assertThat(savedUser.getRole()).isEqualTo(UserRole.CUSTOMER);

            verify(passwordEncoder).encode("Password123!");
            verify(userRepository).existsByUsernameAndHasDeletedFalse("testUser");
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 Username 또는 Nickname이면 예외가 발생")
        void signup_duplicate_username() {
            // given
            UserCreateRequestDto request = new UserCreateRequestDto(
                    "testUser",
                    "Password123!",
                    "테스터",
                    UserRole.CUSTOMER
            );

            when(userRepository.existsByUsernameAndHasDeletedFalse("testUser"))
                    .thenReturn(true);

            when(userRepository.existsByNicknameAndHasDeletedFalse("테스터"))
                    .thenReturn(true);

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.signup(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.DUPLICATE_USERNAME);

            verify(userRepository)
                    .existsByUsernameAndHasDeletedFalse("testUser");

            verify(userRepository)
                    .existsByNicknameAndHasDeletedFalse("테스터");

            verify(userRepository, never())
                    .save(any(UserEntity.class));

            verify(passwordEncoder, never())
                    .encode(anyString());
        }
    }

    @Nested
    @DisplayName("로그인 API 테스트")
    class LoginTest {
        @Test
        @DisplayName("로그인 성공")
        void login_success() {
            UserLoginRequestDto request =
                    new UserLoginRequestDto(
                            "test",
                            "password"
                    );

            UserEntity user =
                    UserEntity.create(
                            "test",
                            "encoded",
                            "닉네임",
                            UserRole.CUSTOMER
                    );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails details =
                    new CustomUserDetails(user);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            details,
                            null,
                            details.getAuthorities()
                    );

            when(authenticationManager.authenticate(any()))
                    .thenReturn(authentication);

            when(jwtProvider.createAccessToken(details))
                    .thenReturn("access");

            when(jwtProvider.createRefreshToken(details))
                    .thenReturn("refresh");

            when(jwtProvider.getRefreshTokenExpiredAt())
                    .thenReturn(LocalDateTime.now().plusSeconds(1209600));

            TokenPairDto result =
                    userService.login(request);

            assertThat(result.accessToken()).isEqualTo("access");
            assertThat(result.refreshToken()).isEqualTo("refresh");

            verify(refreshTokenRepository)
                    .deleteByUserId(1L);

            verify(refreshTokenRepository)
                    .save(any(RefreshTokenEntity.class));
        }

        @Test
        @DisplayName("로그인 실패 - 인증에 실패하면 예외가 발생하고 토큰을 생성하지 않음")
        void login_fail_badCredentials() {
            // given
            UserLoginRequestDto request = new UserLoginRequestDto(
                    "testUser",
                    "wrongPassword"
            );

            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // when & then
            assertThrows(
                    BadCredentialsException.class,
                    () -> userService.login(request)
            );

            // 인증 시도 확인
            verify(authenticationManager).authenticate(any(Authentication.class));

            // 이후 로직이 수행되지 않아야 함
            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
            verify(jwtProvider, never()).getRefreshTokenExpiredAt();

            verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
            verify(refreshTokenRepository, never()).save(any(RefreshTokenEntity.class));

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("재발급 API 테스트")
    class ReIssueTest {
        @Test
        @DisplayName("토큰 재발급 성공")
        void reissue_success() {
            UserEntity user =
                    UserEntity.create(
                            "test",
                            "encoded",
                            "닉네임",
                            UserRole.CUSTOMER
                    );

            ReflectionTestUtils.setField(user, "id", 1L);

            RefreshTokenEntity entity =
                    new RefreshTokenEntity(
                            1L,
                            "oldRefresh",
                            LocalDateTime.now()
                    );

            when(jwtProvider.validateToken(anyString()))
                    .thenReturn(true);

            when(refreshTokenRepository.findByRefreshToken("oldRefresh"))
                    .thenReturn(Optional.of(entity));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            when(jwtProvider.createAccessToken(any()))
                    .thenReturn("newAccess");

            when(jwtProvider.createRefreshToken(any()))
                    .thenReturn("newRefresh");

            when(jwtProvider.getRefreshTokenExpiredAt())
                    .thenReturn(LocalDateTime.now().plusSeconds(1209600));

            TokenPairDto result =
                    userService.reissue("oldRefresh");

            assertThat(result.accessToken()).isEqualTo("newAccess");
            assertThat(result.refreshToken()).isEqualTo("newRefresh");

            verify(refreshTokenRepository)
                    .deleteByUserId(1L);

            verify(refreshTokenRepository)
                    .save(any(RefreshTokenEntity.class));
        }

        @Test
        @DisplayName("토큰 재발급 실패 - RefreshToken이 존재하지 않으면 예외가 발생")
        void reissue_fail_refreshTokenNotFound() {
            // given
            String refreshToken = "invalidRefreshToken";

            when(jwtProvider.validateToken(anyString()))
                    .thenReturn(false);

            when(refreshTokenRepository.findByRefreshToken(refreshToken))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_UNAUTHORIZED);

            verify(jwtProvider).validateToken(refreshToken);
            verify(refreshTokenRepository).findByRefreshToken(refreshToken);

            // 이후 로직은 수행되지 않아야 한다.
            verify(userRepository, never()).findById(anyLong());

            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
            verify(jwtProvider, never()).getRefreshTokenExpiredAt();

            verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
            verify(refreshTokenRepository, never()).save(any(RefreshTokenEntity.class));
        }

        @Test
        @DisplayName("토큰 재발급 실패 - 사용자가 존재하지 않으면 예외가 발생")
        void reissue_fail_userNotFound() {
            // given
            String refreshToken = "validRefreshToken";

            RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(
                    1L,
                    refreshToken,
                    LocalDateTime.now().plusSeconds(60 * 60 * 24 * 14)
            );

            when(jwtProvider.validateToken(anyString()))
                    .thenReturn(false);

            when(refreshTokenRepository.findByRefreshToken(refreshToken))
                    .thenReturn(Optional.of(refreshTokenEntity));

            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_UNAUTHORIZED);

            verify(jwtProvider).validateToken(refreshToken);
            verify(refreshTokenRepository).findByRefreshToken(refreshToken);
            verify(userRepository).findById(1L);

            // 이후 로직은 수행되지 않아야 한다.
            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
            verify(jwtProvider, never()).getRefreshTokenExpiredAt();

            verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
            verify(refreshTokenRepository, never()).save(any(RefreshTokenEntity.class));
        }

        @Test
        @DisplayName("토큰 재발급 실패 - RefreshToken 검증에 실패하면 예외가 발생")
        void reissue_fail_validateToken() {
            // given
            String refreshToken = "invalidRefreshToken";

            ItsHereException expectedException =
                    new ItsHereException(ErrorCode.AUTH_UNAUTHORIZED);

            doThrow(expectedException)
                    .when(jwtProvider)
                    .validateToken(refreshToken);

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_UNAUTHORIZED);

            verify(jwtProvider).validateToken(refreshToken);

            // 이후 로직은 수행되지 않아야 한다.
            verifyNoInteractions(refreshTokenRepository);
            verifyNoInteractions(userRepository);

            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
            verify(jwtProvider, never()).getRefreshTokenExpiredAt();
        }
    }

    @Nested
    @DisplayName("내 정보 조회 API 테스트")
    class GetSelfTest {
        @Test
        @DisplayName("내 정보 조회 성공")
        void getSelf_success() {
            // given
            Long userId = 1L;

            UserEntity user = UserEntity.create(
                    "testUser",
                    "encodedPassword",
                    "테스터",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", userId);

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            // when
            UserSelfGetResponseDto response = userService.getSelf();

            // then
            assertThat(response.username()).isEqualTo("testUser");
            assertThat(response.nickname()).isEqualTo("테스터");
            assertThat(response.role()).isEqualTo(UserRole.CUSTOMER);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("내 정보 조회 실패 - 사용자가 존재하지 않으면 예외가 발생")
        void getSelf_fail_userNotFound() {
            // given
            Long userId = 1L;

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.getSelf()
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findById(userId);
        }
    }
}
