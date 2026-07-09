package com.spring.its_here.domain.area.service;


import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
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
            AreaGetOneRequestDto areagetOneRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional(readOnly = true)
    public AreaGetAllResponseDto getAllArea(AreaGetAllRequestDto areaGetAllRequestDto) {
        return null;
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
}
