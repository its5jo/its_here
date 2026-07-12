package com.spring.its_here.domain.address.controller;

import com.spring.its_here.domain.address.dto.request.AddressCreateRequestDto;
import com.spring.its_here.domain.address.dto.request.AddressUpdateRequestDto;
import com.spring.its_here.domain.address.dto.response.AddressGetResponseDto;
import com.spring.its_here.domain.address.dto.response.AddressResponseDto;
import com.spring.its_here.domain.address.service.AddressService;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponseDto>> create(
            @Valid @RequestBody AddressCreateRequestDto addressCreateRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "주소 생성 성공",
                        addressService.create(addressCreateRequestDto)
                ));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponseDto>> update(
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressUpdateRequestDto addressUpdateRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "주소 수정 성공",
                        addressService.update(addressId, addressUpdateRequestDto)
                ));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID addressId
    ) {
        addressService.delete(addressId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Void>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "주소 목록 조회 성공",
                        addressService.getAll()
                ));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressGetResponseDto>> get(
            @PathVariable UUID addressId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "주소 단건 조회 성공",
                        addressService.get(addressId)
                ));
    }
}
