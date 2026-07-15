package com.spring.its_here.domain.store.controller;

import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.*;
import com.spring.its_here.domain.store.service.StoreService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.response.ApiResponse;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
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
    public ResponseEntity<ApiResponse<StoreGetOneResponseDto>> getOneStore(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID storeId){
        StoreGetOneResponseDto responseDto = storeService.getOneStore(userDetails, storeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 조회 성공", responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<StoreGetAllPageResponseDto>> getAllStores(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Pageable validatedPageable = validatePageable(pageable);
        Page<StoreGetAllResponseDto> responseDtoList = storeService.getAllStores(name, category, validatedPageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 목록 조회 성공", StoreGetAllPageResponseDto.from(responseDtoList)));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreUpdateResponseDto>> updateStore(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID storeId,
            @Valid @RequestBody StoreUpdateRequestDto requestDto){
        StoreUpdateResponseDto responseDto = storeService.updateStore(userDetails, storeId, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("가게 정보 수정 성공", responseDto));
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID storeId){
        storeService.deleteStore(userDetails, storeId);
        return ResponseEntity
                .noContent()
                .build();
    }

    private Pageable validatePageable(Pageable pageable) {
        Set<String> availableSortFields = Set.of("createdAt", "name");

        Sort sort = pageable.getSort();
        for(Sort.Order order : sort){
            String property = order.getProperty();
            if (!availableSortFields.contains(property)) {
                throw new ItsHereException(ErrorCode.STORE_INVALID_SORT_FIELD);
            }
        }

        int size = pageable.getPageSize();
        if ((size == 10 || size == 30 || size == 50)) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                10,
                sort
        );
    }

}
