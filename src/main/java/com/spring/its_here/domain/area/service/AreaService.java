package com.spring.its_here.domain.area.service;


import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.*;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    public AreaCreateResponseDto createArea(AreaCreateRequestDto areaCreateRequestDto) {
        if (areaRepository.existsByCityAndDistrictAndTown(
                areaCreateRequestDto.city(),
                areaCreateRequestDto.district(),
                areaCreateRequestDto.town()
        )) {
            throw new ItsHereException(ErrorCode.AREA_ALREADY_EXISTS);
        }

        Long userId = authenticationFacade.getCurrentUserId();

        Area area = Area.create(
                areaCreateRequestDto.city(),
                areaCreateRequestDto.district(),
                areaCreateRequestDto.town()
        );
        area.assignCreatedBy(userId);

        Area areaSave = areaRepository.save(area);

        return new AreaCreateResponseDto(
                areaSave.getId(),
                areaSave.isHasAvailable()
        );
    }

    @Transactional(readOnly = true)
    public AreaGetOneResponseDto getOneArea(
            UUID areaId
    ) {
        // TODO : errorCode 고치기 (AREA_NOT_FOUND) & findByIdAndDeletedAtIsNull 바꾸기
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_ALREADY_EXISTS));

        return new AreaGetOneResponseDto(
                area.getId(),
                area.getCity(),
                area.getDistrict(),
                area.getTown(),
                area.isHasAvailable(),
                area.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public AreaGetAllResponseDto getAllArea(
            AreaGetAllRequestDto areaGetAllRequestDto,
            Pageable pageable
    ) {
        validatorSize(pageable.getPageSize());
        validatorSortBy(pageable);

        Page<Area> areaPage = areaRepository.searchAreas(
                areaGetAllRequestDto.city(),
                areaGetAllRequestDto.district(),
                areaGetAllRequestDto.town(),
                areaGetAllRequestDto.hasAvailable(),
                pageable
        );

        List<AreaGetAllItemResponseDto> content = areaPage.getContent()
                .stream()
                .map(area -> new AreaGetAllItemResponseDto(
                        area.getId(),
                        area.getCity(),
                        area.getDistrict(),
                        area.getTown(),
                        area.isHasAvailable()
                )).toList();
        AreaPageInfoResponseDto pageInfo = new AreaPageInfoResponseDto(
                areaPage.getNumber(),
                areaPage.getSize(),
                areaPage.getTotalElements(),
                areaPage.getTotalPages(),
                areaPage.hasNext()
        );
        return new AreaGetAllResponseDto(content, pageInfo);
    }

    @Transactional
    public AreaUpdateResponseDto updateArea(
            AreaUpdateRequestDto areaUpdateRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional
    public void deleteArea(UUID areaId) {

    }

    private void validatorSize(int size) {

    }

    private void validatorSortBy(Pageable pageable) {

    }
}
