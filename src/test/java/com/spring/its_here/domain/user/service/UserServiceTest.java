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
            UserSignupRequestDto request = new UserSignupRequestDto(
                    "testUser",
                    "Password123!",
                    "테스터",
                    UserRole.CUSTOMER
            );

            when(userRepository.existsByUsername("testUser"))
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
            verify(userRepository).existsByUsername("testUser");
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 Username 또는 Nickname이면 예외가 발생")
        void signup_duplicate_username() {
            // given
            UserSignupRequestDto request = new UserSignupRequestDto(
                    "testUser",
                    "Password123!",
                    "테스터",
                    UserRole.CUSTOMER
            );

            when(userRepository.existsByUsername("testUser"))
                    .thenReturn(true);

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.signup(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.DUPLICATE_USERNAME);

            verify(userRepository).existsByUsername("testUser");
            verify(userRepository, never()).save(any(UserEntity.class));
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("로그인 API 테스트")
    class LoginTest {
        @Test
        @DisplayName("로그인 성공")
        void login_success() {
            // given
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

            CustomUserDetails userDetails = new CustomUserDetails(user);


            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class
            )))
                    .thenReturn(authentication);

            when(jwtProvider.createAccessToken(any(CustomUserDetails.class)))
                    .thenReturn("access");
            when(jwtProvider.createRefreshToken(any(CustomUserDetails.class)))
                    .thenReturn("refresh");


            // when
            TokenPairDto result = userService.login(request);


            // then
            assertThat(result.accessToken()).isEqualTo("access");
            assertThat(result.refreshToken()).isEqualTo("refresh");


            verify(authenticationManager)
                    .authenticate(any(
                            UsernamePasswordAuthenticationToken.class
                    ));
            verify(jwtProvider)
                    .createAccessToken(any(CustomUserDetails.class));
            verify(jwtProvider)
                    .createRefreshToken(any(CustomUserDetails.class));
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

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("토큰 재발급 API 테스트")
    class ReissueTest {

        @Test
        @DisplayName("토큰 재발급 성공")
        void reissue_success() {
            // given
            String refreshToken = "refreshToken";

            UserEntity user = UserEntity.create(
                    "test",
                    "encodedPassword",
                    "테스터",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            when(jwtProvider.validateToken(refreshToken))
                    .thenReturn(true);

            when(jwtProvider.getUserId(refreshToken))
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.of(user));

            when(jwtProvider.createAccessToken(any(CustomUserDetails.class)))
                    .thenReturn("newAccessToken");

            when(jwtProvider.createRefreshToken(any(CustomUserDetails.class)))
                    .thenReturn("newRefreshToken");

            // when
            TokenPairDto response = userService.reissue(refreshToken);

            // then
            assertThat(response.accessToken()).isEqualTo("newAccessToken");
            assertThat(response.refreshToken()).isEqualTo("newRefreshToken");

            verify(jwtProvider).validateToken(refreshToken);
            verify(jwtProvider).getUserId(refreshToken);
            verify(userRepository).findByIdAndHasDeletedFalse(1L);
            verify(jwtProvider).createAccessToken(any(CustomUserDetails.class));
            verify(jwtProvider).createRefreshToken(any(CustomUserDetails.class));
        }

        @Test
        @DisplayName("토큰 재발급 실패 - Refresh Token 검증 실패")
        void reissue_fail_invalidRefreshToken() {
            // given
            String refreshToken = "invalidRefreshToken";

            when(jwtProvider.validateToken(refreshToken))
                    .thenReturn(false);

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_UNAUTHORIZED);

            verify(jwtProvider).validateToken(refreshToken);
            verify(jwtProvider, never()).getUserId(anyString());
            verify(userRepository, never()).findByIdAndHasDeletedFalse(anyLong());
            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
        }

        @Test
        @DisplayName("토큰 재발급 실패 - 사용자가 존재하지 않으면 예외가 발생")
        void reissue_fail_userNotFound() {
            // given
            String refreshToken = "refreshToken";

            when(jwtProvider.validateToken(refreshToken))
                    .thenReturn(true);

            when(jwtProvider.getUserId(refreshToken))
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(jwtProvider).validateToken(refreshToken);
            verify(jwtProvider).getUserId(refreshToken);
            verify(userRepository).findByIdAndHasDeletedFalse(1L);

            verify(jwtProvider, never()).createAccessToken(any());
            verify(jwtProvider, never()).createRefreshToken(any());
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

            when(userRepository.findByIdAndHasDeletedFalse(userId))
                    .thenReturn(Optional.of(user));

            // when
            UserSelfGetResponseDto response = userService.getSelf();

            // then
            assertThat(response.username()).isEqualTo("testUser");
            assertThat(response.nickname()).isEqualTo("테스터");
            assertThat(response.role()).isEqualTo(UserRole.CUSTOMER);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
        }

        @Test
        @DisplayName("내 정보 조회 실패 - 삭제된 사용자 또는 존재하지 않는 사용자이면 예외가 발생")
        void getSelf_fail_deletedOrNotFoundUser() {
            // given
            Long userId = 1L;

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findByIdAndHasDeletedFalse(userId))
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
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
        }
    }

    @Nested
    @DisplayName("내 정보 삭제 API 테스트")
    class DeleteSelfTest {

        @Test
        @DisplayName("내 정보 삭제 성공")
        void delete_success() {
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

            when(userRepository.findByIdAndHasDeletedFalse(userId))
                    .thenReturn(Optional.of(user));

            // when
            userService.delete();

            // then
            assertThat(user.getHasDeleted()).isTrue();
            assertThat(user.getDeletedAt()).isNotNull();
            assertThat(user.getDeletedBy()).isEqualTo(userId);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
        }

        @Test
        @DisplayName("내 정보 삭제 실패 - 삭제된 사용자 또는 존재하지 않는 사용자이면 예외가 발생")
        void delete_fail_userNotFound() {
            // given
            Long userId = 1L;

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findByIdAndHasDeletedFalse(userId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.delete()
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
        }
    }

    @Nested
    @DisplayName("내 정보 수정 API 테스트")
    class UpdateSelfTest {

        @Test
        @DisplayName("내 정보 수정 성공")
        void update_success() {
            // given
            Long userId = 1L;

            UserEntity user = UserEntity.create(
                    "testUser",
                    "encodedPassword",
                    "기존닉네임",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", userId);

            UserUpdateRequestDto request =
                    new UserUpdateRequestDto(
                            "newPassword",
                            "새닉네임"
                    );

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findByIdAndHasDeletedFalse(userId))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode("newPassword"))
                    .thenReturn("encodedNewPassword");

            // when
            UserResponseDto response = userService.update(request);

            // then
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
            assertThat(user.getNickname()).isEqualTo("새닉네임");

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
            verify(passwordEncoder).encode("newPassword");
        }

        @Test
        @DisplayName("내 정보 수정 실패 - 삭제된 사용자 또는 존재하지 않는 사용자이면 예외가 발생")
        void update_fail_userNotFound() {
            // given
            Long userId = 1L;

            UserUpdateRequestDto request =
                    new UserUpdateRequestDto(
                            "newPassword",
                            "새닉네임"
                    );

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(userId);

            when(userRepository.findByIdAndHasDeletedFalse(userId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.update(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(userId);
            verify(passwordEncoder, never()).encode(anyString());
        }
    }
}
