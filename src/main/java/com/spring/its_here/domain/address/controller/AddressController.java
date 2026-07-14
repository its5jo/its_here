package com.spring.its_here.domain.address.controller;

import com.spring.its_here.domain.address.dto.request.AddressCreateRequestDto;
import com.spring.its_here.domain.address.dto.request.AddressUpdateRequestDto;
import com.spring.its_here.domain.address.dto.response.AddressGetResponseDto;
import com.spring.its_here.domain.address.dto.response.AddressResponseDto;
import com.spring.its_here.domain.address.service.AddressService;
import com.spring.its_here.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "주소", description = "배달 주소 관련 API 입니다.")
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @Operation(
            summary = "주소 생성",
            description = "Customer 권한을 가진 사용자가 새로운 배달 주소를 생성합니다."
    )
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

    @Operation(
            summary = "주소 수정",
            description = "Customer 권한을 가진 사용자가 자신의 배달 주소를 수정합니다."
    )
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

    @Operation(
            summary = "주소 삭제",
            description = "Customer 권한을 가진 사용자가 자신의 배달 주소를 삭제합니다."
    )
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID addressId
    ) {
        addressService.delete(addressId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
            summary = "주소 목록 조회",
            description = "Customer 권한을 가진 사용자가 자신의 배달 주소 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Void>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "주소 목록 조회 성공",
                        addressService.getAll()
                ));
    }

    @Operation(
            summary = "주소 단건 조회",
            description = "Customer 권한을 가진 사용자가 자신의 배달 주소를 조회합니다."
    )
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
