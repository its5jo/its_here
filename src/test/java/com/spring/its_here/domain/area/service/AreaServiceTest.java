package com.spring.its_here.domain.area.service;

import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Area createArea(
            UUID id,
            String city,
            String district,
            String town,
            Instant createdAt
    ) {
        Area area = Area.create(city, district, town);

        ReflectionTestUtils.setField(area, "id", id);
        ReflectionTestUtils.setField(area, "createdAt", createdAt);

        return area;
    }

    @Nested
    @DisplayName("지역 생성")
    class create {
        @Test
        @DisplayName("성공")
        void createArea_success() {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            Area areaSave = createArea(
                    areaId,
                    "city",
                    "district",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
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
        void createArea_duplicate() {
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

    @Nested
    @DisplayName("지역 조회")
    class get {
        @Test
        @DisplayName("단건조회 성공")
        void getOneArea_success() {
            Area area = createArea(
                    areaId,
                    "city",
                    "district",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
            );
            given(areaRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.of(area));

            AreaGetOneResponseDto areaGetOneResponseDto = areaService.getOneArea(areaId);

            assertThat(areaGetOneResponseDto).isNotNull();
            assertThat(areaGetOneResponseDto.areaId()).isEqualTo(areaId);
            assertThat(areaGetOneResponseDto.city()).isEqualTo("city");
            assertThat(areaGetOneResponseDto.district()).isEqualTo("district");
            assertThat(areaGetOneResponseDto.town()).isEqualTo("town");
            assertThat(areaGetOneResponseDto.hasAvailable()).isFalse();
            assertThat(areaGetOneResponseDto.createdAt()).isEqualTo(Instant.parse("2026-07-12T06:00:00Z")
            );

            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);
        }

        @Test
        @DisplayName("존재하지 않는 지역이면 예외")
        void getOneArea_not_found() {
            ItsHereException itsHereException = assertThrows(
                    ItsHereException.class,
                    () -> areaService.getOneArea(areaId)
            );
            assertThat(itsHereException.getErrorCode()).isEqualTo(ErrorCode.AREA_NOT_FOUND);

            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);
        }

        @Test
        @DisplayName("전체조회 성공")
        void getAllArea_success() {
            UUID firstAreaId = UUID.randomUUID();
            UUID secondAreaId = UUID.randomUUID();

            Area firstArea = createArea(
                    firstAreaId,
                    "city",
                    "district1",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
            );
            Area secondArea = createArea(
                    secondAreaId,
                    "city",
                    "district2",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
            );

            AreaGetAllRequestDto areaGetAllRequestDto = new AreaGetAllRequestDto(
                    "city",
                    "district",
                    "town",
                    true
            );

            Pageable pageable = PageRequest.of(
                    0,
                    10,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Page<Area> areaPage = new PageImpl<>(
                    List.of(firstArea, secondArea),
                    pageable,
                    12
            );

            given(areaRepository.searchAreas(
                    areaGetAllRequestDto.city(),
                    areaGetAllRequestDto.district(),
                    areaGetAllRequestDto.town(),
                    areaGetAllRequestDto.hasAvailable(),
                    pageable
            )).willReturn(areaPage);

            AreaGetAllResponseDto areaGetAllResponseDto = areaService.getAllArea(
                    areaGetAllRequestDto,
                    pageable
            );

            assertThat(areaGetAllResponseDto).isNotNull();
            assertThat(areaGetAllResponseDto.content()).hasSize(2);
            assertThat(areaGetAllResponseDto.content().get(0).areaId()).isEqualTo(firstAreaId);
            assertThat(areaGetAllResponseDto.areaPageInfoResponseDto().totalElements()).isEqualTo(12L);
            assertThat(areaGetAllResponseDto.areaPageInfoResponseDto().hasNext()).isTrue();

            verify(areaRepository).searchAreas(
                    areaGetAllRequestDto.city(),
                    areaGetAllRequestDto.district(),
                    areaGetAllRequestDto.town(),
                    areaGetAllRequestDto.hasAvailable(),
                    pageable
            );
        }
    }
}