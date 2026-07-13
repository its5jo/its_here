package com.spring.its_here.domain.address.service;

import com.spring.its_here.domain.address.dto.request.AddressCreateRequestDto;
import com.spring.its_here.domain.address.dto.response.AddressResponseDto;
import com.spring.its_here.domain.address.entity.Address;
import com.spring.its_here.domain.address.repository.AddressRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private AddressService addressService;

    @Nested
    @DisplayName("주소 생성 API 테스트")
    class CreateTest {
        @Test
        @DisplayName("주소 생성 성공 - 신규 주소")
        void create_success() {
            // given
            AddressCreateRequestDto request =
                    new AddressCreateRequestDto("서울특별시 강남구");

            UserEntity user = UserEntity.create(
                    "test",
                    "encodedPassword",
                    "테스터",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            UUID addressId = UUID.randomUUID();

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.of(user));

            when(addressRepository.existsByUserIdAndAddressAndDeletedAtNull(
                    1L,
                    request.address()
            )).thenReturn(false);

            when(addressRepository.findByUserIdAndAddressAndDeletedAtNotNull(
                    1L,
                    request.address()
            )).thenReturn(Optional.empty());

            when(addressRepository.save(any(Address.class)))
                    .thenAnswer(invocation -> {
                        Address address = invocation.getArgument(0);
                        ReflectionTestUtils.setField(address, "id", addressId);
                        return address;
                    });

            // when
            AddressResponseDto response = addressService.create(request);

            // then
            assertThat(response.addressId()).isEqualTo(addressId);

            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("주소 생성 성공 - 삭제된 주소 복구")
        void create_success_restoreDeletedAddress() {
            // given
            AddressCreateRequestDto request =
                    new AddressCreateRequestDto("서울특별시 강남구");

            UserEntity user = UserEntity.create(
                    "test",
                    "encodedPassword",
                    "테스터",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            UUID addressId = UUID.randomUUID();

            Address deletedAddress = Address.create(
                    request.address(),
                    user
            );

            ReflectionTestUtils.setField(deletedAddress, "id", addressId);
            deletedAddress.delete(1L);

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.of(user));

            when(addressRepository.existsByUserIdAndAddressAndDeletedAtNull(
                    1L,
                    request.address()
            )).thenReturn(false);

            when(addressRepository.findByUserIdAndAddressAndDeletedAtNotNull(
                    1L,
                    request.address()
            )).thenReturn(Optional.of(deletedAddress));

            // when
            AddressResponseDto response = addressService.create(request);

            // then
            assertThat(response.addressId()).isEqualTo(addressId);

            verify(addressRepository, never()).save(any(Address.class));
        }

        @Test
        @DisplayName("주소 생성 실패 - 이미 등록된 주소")
        void create_fail_duplicateAddress() {
            // given
            AddressCreateRequestDto request =
                    new AddressCreateRequestDto("서울특별시 강남구");

            UserEntity user = UserEntity.create(
                    "test",
                    "encodedPassword",
                    "테스터",
                    UserRole.CUSTOMER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.of(user));

            when(addressRepository.existsByUserIdAndAddressAndDeletedAtNull(
                    1L,
                    request.address()
            )).thenReturn(true);

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> addressService.create(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.ADDRESS_ALREADY_EXISTS);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(1L);
            verify(addressRepository)
                    .existsByUserIdAndAddressAndDeletedAtNull(
                            1L,
                            request.address()
                    );

            verify(addressRepository, never())
                    .findByUserIdAndAddressAndDeletedAtNotNull(anyLong(), anyString());

            verify(addressRepository, never()).save(any(Address.class));
        }

        @Test
        @DisplayName("주소 생성 실패 - 사용자가 존재하지 않음")
        void create_fail_userNotFound() {
            // given
            AddressCreateRequestDto request =
                    new AddressCreateRequestDto("서울특별시 강남구");

            when(authenticationFacade.getCurrentUserId())
                    .thenReturn(1L);

            when(userRepository.findByIdAndHasDeletedFalse(1L))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> addressService.create(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(authenticationFacade).getCurrentUserId();
            verify(userRepository).findByIdAndHasDeletedFalse(1L);

            verify(addressRepository, never())
                    .existsByUserIdAndAddressAndDeletedAtNull(anyLong(), anyString());

            verify(addressRepository, never())
                    .findByUserIdAndAddressAndDeletedAtNotNull(anyLong(), anyString());

            verify(addressRepository, never()).save(any(Address.class));
        }
    }
}
