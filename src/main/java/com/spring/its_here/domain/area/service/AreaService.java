package com.spring.its_here.domain.area.service;


import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.*;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.response.OffsetPageInfo;
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

    @Transactional
    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    public AreaCreateResponseDto createArea(
            AreaCreateRequestDto areaCreateRequestDto
    ) {
        validateDuplicateAreaForCreate(
                areaCreateRequestDto.city(),
                areaCreateRequestDto.district(),
                areaCreateRequestDto.town()
        );

        Area area = Area.create(
                areaCreateRequestDto.city(),
                areaCreateRequestDto.district(),
                areaCreateRequestDto.town()
        );

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
        Area area = findByIdAndDeletedAtIsNull(areaId);

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
        OffsetPageInfo pageInfo = OffsetPageInfo.from(areaPage);

        return new AreaGetAllResponseDto(
                content,
                pageInfo
        );
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    @Transactional
    public AreaUpdateResponseDto updateArea(
            AreaUpdateRequestDto areaUpdateRequestDto,
            UUID areaId
    ) {
        Area area = findByIdAndDeletedAtIsNull(areaId);
        validateDuplicateAreaForUpdate(
                areaUpdateRequestDto.city(),
                areaUpdateRequestDto.district(),
                areaUpdateRequestDto.town(),
                areaId
        );

        area.updatedArea(
                areaUpdateRequestDto.city(),
                areaUpdateRequestDto.district(),
                areaUpdateRequestDto.town(),
                areaUpdateRequestDto.hasAvailable()
        );

        return new AreaUpdateResponseDto(area.getId());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','MASTER')")
    @Transactional
    public void deleteArea(
            Long userId,
            UUID areaId
    ) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_NOT_FOUND));

        if (area.isDeleted()) {
            throw new ItsHereException(ErrorCode.AREA_ALREADY_DELETED);
        }

        area.delete(userId);
    }

    private Area findByIdAndDeletedAtIsNull(UUID areaId) {
        return areaRepository.findByIdAndDeletedAtIsNull(areaId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.AREA_NOT_FOUND));
    }

    private void validateDuplicateAreaForCreate(
            String city,
            String district,
            String town
    ) {
        if (areaRepository.existsByCityAndDistrictAndTown(
                city,
                district,
                town
        )) {
            throw new ItsHereException(ErrorCode.AREA_ALREADY_EXISTS);
        }
    }

    private void validateDuplicateAreaForUpdate(
            String city,
            String district,
            String town,
            UUID areaId
    ) {
        if (areaRepository.existsByCityAndDistrictAndTownAndIdNot(
                city,
                district,
                town,
                areaId
        )) {
            throw new ItsHereException(ErrorCode.AREA_ALREADY_EXISTS);
        }
    }
}
