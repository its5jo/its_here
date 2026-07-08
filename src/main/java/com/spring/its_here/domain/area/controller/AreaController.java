package com.spring.its_here.domain.area.controller;


import com.spring.its_here.domain.area.dto.request.CreateAreaRequestDto;
import com.spring.its_here.domain.area.dto.request.GetAllAreaRequestDto;
import com.spring.its_here.domain.area.dto.request.GetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.UpdateAreaRequestDto;
import com.spring.its_here.domain.area.dto.response.CreateAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.GetAllAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.GetOneAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.UpdateAreaResponseDto;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<CreateAreaResponseDto>> createArea(
            @RequestBody CreateAreaRequestDto createAreaRequestDto
    ) {
        CreateAreaResponseDto createAreaResponseDto = areaService.createArea(createAreaRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("서비스 지역 등록 성공", createAreaResponseDto));
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<ApiResponse<GetOneAreaResponseDto>> getOneArea(
            @RequestBody GetOneRequestDto getOneRequestDto,
            @PathVariable("areaId") UUID areaId
    ) {
        GetOneAreaResponseDto getOneAreaResponseDto = areaService.getOneArea(
                getOneRequestDto,
                areaId
        );

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 상세 조회 성공", getOneAreaResponseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetAllAreaResponseDto>> getAllArea(
            @RequestBody GetAllAreaRequestDto getAllAreaRequestDto
    ) {
        GetAllAreaResponseDto getAllAreaResponseDto = areaService.getAllArea(getAllAreaRequestDto);

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 전체 조회 성공", getAllAreaResponseDto));
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<ApiResponse<UpdateAreaResponseDto>> updateArea(
            @RequestBody UpdateAreaRequestDto updateAreaRequestDto,
            @PathVariable("areaId") UUID areaId
    ) {
        UpdateAreaResponseDto updateAreaResponseDto = areaService.updateArea(updateAreaRequestDto, areaId);

        return ResponseEntity.ok(ApiResponse.success("서비스 지역 수정 성공", updateAreaResponseDto));
    }

    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteArea(
            @PathVariable("areaId") UUID areaId
    ) {
        areaService.deleteArea(areaId);
        return ResponseEntity.ok().build();
    }
}
