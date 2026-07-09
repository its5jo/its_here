package com.spring.its_here.domain.area.service;

import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {
    @Mock
    AreaRepository areaRepository;

    @Mock
    AuthenticationFacade authenticationFacade;

    @InjectMocks
    AreaService areaService;

    UUID areaId = UUID.randomUUID();

    @Nested
    @DisplayName("지역 생성")
    class create {
        @Test
        @DisplayName("성공")
        void create_success() {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            Area areaSave = Area.create(
                    "city",
                    "district",
                    "town"
            );

            ReflectionTestUtils.setField(areaSave, "id", areaId);

            given(authenticationFacade.getCurrentUserId()).willReturn(1L);
            given(areaRepository.save(any(Area.class))).willReturn(areaSave);

            AreaCreateResponseDto areaCreateResponseDto = areaService.createArea(areaCreateRequestDto);

            assertThat(areaCreateResponseDto).isNotNull();
            assertThat(areaCreateResponseDto.areaId()).isEqualTo(areaId);

            verify(authenticationFacade).getCurrentUserId();
            verify(areaRepository).save(any(Area.class));
        }

        @Test
        @DisplayName("지역 중복 예외")
        void create_duplicate() {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            given(areaRepository.existsByCityAndDistrictAndTown(
                    areaCreateRequestDto.city(),
                    areaCreateRequestDto.district(),
                    areaCreateRequestDto.town()
            )).willReturn(true);

            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> areaService.createArea(areaCreateRequestDto)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AREA_ALREADY_EXISTS);

            verify(areaRepository).existsByCityAndDistrictAndTown(
                    areaCreateRequestDto.city(),
                    areaCreateRequestDto.district(),
                    areaCreateRequestDto.town()
            );
            verify(areaRepository, never()).save(any(Area.class));
            verify(authenticationFacade, never()).getCurrentUserId();
        }
    }
}