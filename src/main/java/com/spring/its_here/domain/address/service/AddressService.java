package com.spring.its_here.domain.address.service;

import com.spring.its_here.domain.address.dto.request.AddressCreateRequestDto;
import com.spring.its_here.domain.address.dto.request.AddressUpdateRequestDto;
import com.spring.its_here.domain.address.dto.response.AddressGetResponseDto;
import com.spring.its_here.domain.address.dto.response.AddressResponseDto;
import com.spring.its_here.domain.address.entity.Address;
import com.spring.its_here.domain.address.repository.AddressRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationFacade authenticationFacade;

    private UserEntity getCurrentUser() {
        // 현재 로그인된 사용자 PK 가져오기
        Long userId = authenticationFacade.getCurrentUserId();

        // PK를 이용하여 최신 사용자 정보를 조회
        return userRepository.findByIdAndHasDeletedFalse(userId)
                .orElseThrow(() ->
                        new ItsHereException(ErrorCode.USER_NOT_FOUND)
                );
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public AddressResponseDto create(AddressCreateRequestDto request) {
        // 현재 인증된 사용자 반환
        UserEntity currentUser = getCurrentUser();

        // 이미 등록된 주소인지 확인
        if (addressRepository
                .existsByUserIdAndAddressAndDeletedAtNull(
                    currentUser.getId(),
                    request.address())
        ) {
            throw new ItsHereException(ErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        // 삭제된 주소 복구
        Optional<Address> deletedAddress =
                addressRepository.findByUserIdAndAddressAndDeletedAtNotNull(
                        currentUser.getId(),
                        request.address()
                );

        // 삭제된 주소가 있을 경우
        if (deletedAddress.isPresent()) {
            Address address = deletedAddress.get();
            address.restore();

            return new AddressResponseDto(address.getId());
        }

        // 신규 생성
        Address address = Address.create(
                request.address(),
                currentUser
        );

        addressRepository.save(address);

        return new AddressResponseDto(address.getId());
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public AddressResponseDto update(UUID addressId, AddressUpdateRequestDto addressUpdateRequestDto) {
        // 현재 인증된 사용자 반환
        UserEntity currentUser = getCurrentUser();

        // 삭제된 주소인지 확인
        Address address = addressRepository.findByIdAndDeletedAtNull(addressId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.ADDRESS_NOT_FOUND));

        // 주소를 생성한 사용자가 맞는지 확인
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new ItsHereException(ErrorCode.AUTH_FORBIDDEN);
        }

        // 주소 수정
        address.update(addressUpdateRequestDto.address());

        return new AddressResponseDto(address.getId());
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public void delete(UUID addressId) {}

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional(readOnly = true)
    public AddressGetResponseDto get(UUID addressId) {
        return null;
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional(readOnly = true)
    public Void getAll() {
        return null;
    }
}
