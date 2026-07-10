package com.spring.its_here.domain.area.controller;


import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController {
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
            @RequestBody AreaGetOneRequestDto areaGetOneRequestDto,
            @PathVariable("areaId") UUID areaId
    ) {
        AreaGetOneResponseDto areaGetOneResponseDto = areaService.getOneArea(
                areaGetOneRequestDto,
                areaId
        );

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 상세 조회 성공", areaGetOneResponseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AreaGetAllResponseDto>> getAllArea(
            @RequestBody AreaGetAllRequestDto areaGetAllRequestDto
    ) {
        AreaGetAllResponseDto areaGetAllResponseDto = areaService.getAllArea(areaGetAllRequestDto);

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
}
