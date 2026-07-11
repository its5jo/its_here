package com.spring.its_here.domain.store.controller;

import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
import com.spring.its_here.domain.store.service.StoreService;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/stores")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<ApiResponse<StoreCreateResponseDto>> createStore(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody StoreCreateRequestDto requestDto){
        StoreCreateResponseDto responseDto = storeService.createStore(userDetails, requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("가게 등록 성공", responseDto));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreGetOneResponseDto>> getOneStore(@PathVariable UUID storeId){
        StoreGetOneResponseDto responseDto = storeService.getOneStore(storeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 조회 성공", responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StoreGetAllResponseDto>>> getAllStores(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable){
        Page<StoreGetAllResponseDto> responseDtoList = storeService.getAllStores(name, category, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 목록 조회 성공", responseDtoList));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreUpdateResponseDto>> updateStore(
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto requestDto){
        StoreUpdateResponseDto responseDto = storeService.updateStore(storeId, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 정보 수정 성공", responseDto));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId){
        storeService.deleteStore(storeId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
