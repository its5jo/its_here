package com.spring.its_here.domain.user.service;

import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
        @DisplayName("중복된 Username이면 예외가 발생")
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

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> userService.signup(request)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.DUPLICATE_USERNAME);

            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }
}
