package com.spring.its_here.domain.area.service;


import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
import com.spring.its_here.domain.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaService {
    private AreaRepository areaRepository;

    @Transactional
    public AreaCreateResponseDto areaCreate(AreaCreateRequestDto areaCreateRequestDto) {
        return null;
    }

    @Transactional(readOnly = true)
    public AreaGetOneResponseDto areaGetOne(
            AreaGetOneRequestDto areagetOneRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional(readOnly = true)
    public AreaGetAllResponseDto areaGetAll(AreaGetAllRequestDto areaGetAllRequestDto) {
        return null;
    }

    @Transactional
    public AreaUpdateResponseDto areaUpdate(
            AreaUpdateRequestDto areaUpdateRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional
    public void areaDelete(UUID areaId) {

    }
}
