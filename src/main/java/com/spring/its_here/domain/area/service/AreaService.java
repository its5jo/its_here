package com.spring.its_here.domain.area.service;


import com.spring.its_here.domain.area.dto.request.CreateAreaRequestDto;
import com.spring.its_here.domain.area.dto.request.GetAllAreaRequestDto;
import com.spring.its_here.domain.area.dto.request.GetOneRequestDto;
import com.spring.its_here.domain.area.dto.request.UpdateAreaRequestDto;
import com.spring.its_here.domain.area.dto.response.CreateAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.GetAllAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.GetOneAreaResponseDto;
import com.spring.its_here.domain.area.dto.response.UpdateAreaResponseDto;
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
    public CreateAreaResponseDto createArea(CreateAreaRequestDto createAreaRequestDto) {
        return null;
    }

    @Transactional(readOnly = true)
    public GetOneAreaResponseDto getOneArea(
            GetOneRequestDto getOneRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional(readOnly = true)
    public GetAllAreaResponseDto getAllArea(GetAllAreaRequestDto getAllAreaRequestDto) {
        return null;
    }

    @Transactional
    public UpdateAreaResponseDto updateArea(
            UpdateAreaRequestDto updateAreaRequestDto,
            UUID areaId
    ) {
        return null;
    }

    @Transactional
    public void deleteArea(UUID areaId) {

    }
}
