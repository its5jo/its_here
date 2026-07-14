package com.spring.its_here.domain.area.controller;


import com.spring.its_here.domain.area.controller.docs.AreaApi;
import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController implements AreaApi {
    private final AreaService areaService;

    @PostMapping
    public ResponseEntity<ApiResponse<AreaCreateResponseDto>> createArea(
            @Valid @RequestBody AreaCreateRequestDto areaCreateRequestDto
    ) {
        AreaCreateResponseDto areaCreateResponseDto = areaService.createArea(areaCreateRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("서비스 지역 등록 성공", areaCreateResponseDto));
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<ApiResponse<AreaGetOneResponseDto>> getOneArea(
            @PathVariable("areaId") UUID areaId
    ) {
        AreaGetOneResponseDto areaGetOneResponseDto = areaService.getOneArea(areaId);

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 상세 조회 성공", areaGetOneResponseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AreaGetAllResponseDto>> getAllArea(
            @ModelAttribute AreaGetAllRequestDto areaGetAllRequestDto,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        validatorSortBy(pageable);
        Pageable normalizeSize = normalizeSize(pageable);

        AreaGetAllResponseDto areaGetAllResponseDto = areaService.getAllArea(
                areaGetAllRequestDto,
                normalizeSize
        );

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 전체 조회 성공", areaGetAllResponseDto));
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<ApiResponse<AreaUpdateResponseDto>> updateArea(
            @RequestBody AreaUpdateRequestDto areaUpdateRequestDto,
            @PathVariable("areaId") UUID areaId
    ) {
        AreaUpdateResponseDto areaUpdateResponseDto = areaService.updateArea(areaUpdateRequestDto, areaId);

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 수정 성공", areaUpdateResponseDto));
    }

    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteArea(
            @PathVariable("areaId") UUID areaId
    ) {
        areaService.deleteArea(areaId);
        return ResponseEntity.ok().build();
    }


    private Pageable normalizeSize(Pageable pageable) {
        int size = pageable.getPageSize();

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private void validatorSortBy(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!order.getProperty().equals("createdAt")) {
                throw new ItsHereException(ErrorCode.AREA_INVALID_SORT_BY);
            }
        }
    }
}
