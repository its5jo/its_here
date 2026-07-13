package com.spring.its_here.domain.address.service;

import com.spring.its_here.domain.address.dto.request.AddressCreateRequestDto;
import com.spring.its_here.domain.address.dto.request.AddressUpdateRequestDto;
import com.spring.its_here.domain.address.dto.response.AddressGetResponseDto;
import com.spring.its_here.domain.address.dto.response.AddressResponseDto;
import com.spring.its_here.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressResponseDto create(AddressCreateRequestDto addressCreateRequestDto) {
        return null;
    }

    public AddressResponseDto update(UUID addressId, AddressUpdateRequestDto addressUpdateRequestDto) {
        return null;
    }

    public void delete(UUID addressId) {}

    public AddressGetResponseDto get(UUID addressId) {
        return null;
    }

    public Void getAll() {
        return null;
    }
}
