package com.spring.its_here.domain.area.service;

import com.spring.its_here.domain.area.dto.request.AreaCreateRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.request.AreaUpdateRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaCreateResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaUpdateResponseDto;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {
    @Mock
    AreaRepository areaRepository;

    @InjectMocks
    AreaService areaService;

    UUID areaId = UUID.randomUUID();

    private Area createTestArea(
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
    class createArea {
        @Test
        @DisplayName("성공")
        void createArea_success() {
            AreaCreateRequestDto areaCreateRequestDto = new AreaCreateRequestDto(
                    "city",
                    "district",
                    "town"
            );
            Area areaSave = createTestArea(
                    areaId,
                    "city",
                    "district",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
            );

            ReflectionTestUtils.setField(areaSave, "id", areaId);

            given(areaRepository.save(any(Area.class))).willReturn(areaSave);

            AreaCreateResponseDto areaCreateResponseDto = areaService.createArea(areaCreateRequestDto);

            assertThat(areaCreateResponseDto).isNotNull();
            assertThat(areaCreateResponseDto.areaId()).isEqualTo(areaId);

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
        }
    }

    @Nested
    @DisplayName("지역 조회")
    class getArea {
        @Test
        @DisplayName("단건조회 성공")
        void getOneArea_success() {
            Area area = createTestArea(
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

            Area firstArea = createTestArea(
                    firstAreaId,
                    "city",
                    "district1",
                    "town",
                    Instant.parse("2026-07-12T06:00:00Z")
            );
            Area secondArea = createTestArea(
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
            assertThat(areaGetAllResponseDto.pageInfo().totalCount()).isEqualTo(12L);
            assertThat(areaGetAllResponseDto.pageInfo().hasNext()).isTrue();

            verify(areaRepository).searchAreas(
                    areaGetAllRequestDto.city(),
                    areaGetAllRequestDto.district(),
                    areaGetAllRequestDto.town(),
                    areaGetAllRequestDto.hasAvailable(),
                    pageable
            );
        }
    }

    @Nested
    @DisplayName("지역 수정")
    class updateArea {
        @Test
        @DisplayName("지역수정 성공")
        void updateArea_success() {
            Area area = createTestArea(
                    areaId,
                    "city",
                    "district",
                    "tonw",
                    Instant.parse("2026-07-15T01:50:00Z")
            );
            AreaUpdateRequestDto areaUpdateRequestDto = new AreaUpdateRequestDto(
                    "city",
                    "district",
                    "town",
                    true
            );
            given(areaRepository.findByIdAndDeletedAtIsNull(areaId)).willReturn(Optional.of(area));
            given(areaRepository.existsByCityAndDistrictAndTownAndIdNot(
                    areaUpdateRequestDto.city(),
                    areaUpdateRequestDto.district(),
                    areaUpdateRequestDto.town(),
                    areaId
            )).willReturn(false);

            AreaUpdateResponseDto areaUpdateResponseDto = areaService.updateArea(
                    areaUpdateRequestDto,
                    areaId
            );
            assertThat(areaUpdateResponseDto.areaId()).isEqualTo(areaId);
            assertThat(area.getCity()).isEqualTo("city");
            assertThat(area.getDistrict()).isEqualTo("district");
            assertThat(area.getTown()).isEqualTo("town");
            assertThat(area.isHasAvailable()).isTrue();

            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);

            verify(areaRepository).existsByCityAndDistrictAndTownAndIdNot(
                    areaUpdateRequestDto.city(),
                    areaUpdateRequestDto.district(),
                    areaUpdateRequestDto.town(),
                    areaId
            );
        }
    }

    @Nested
    @DisplayName("지역 삭제")
    class deleteArea {
        Long userId = 1L;

        @Test
        @DisplayName("지역삭제 성공")
        void deleteReview_success() {
            Area area = createTestArea(
                    areaId,
                    "city",
                    "district",
                    "town",
                    Instant.parse("2026-07-15T01:34:59Z")
            );

            given(areaRepository.findById(areaId)).willReturn(Optional.of(area));
            areaService.deleteArea(userId, areaId);

            assertThat(area.getDeletedAt()).isNotNull();
            assertThat(area.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("지역삭제 존재하지 않는 지역 삭제 시 예외")
        void deleteArea_not_found() {
            given(areaRepository.findById(areaId)).willReturn(Optional.empty());

            assertThatThrownBy(
                    () -> areaService.deleteArea(
                            userId,
                            areaId
                    )).isInstanceOf(ItsHereException.class)
                    .hasMessage(ErrorCode.AREA_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("지역삭제 이미 삭제된 지역 삭제 시 예외")
        void deleteArea_already_deleted() {
            Area area = createTestArea(
                    areaId,
                    "city",
                    "district",
                    "town",
                    Instant.parse("2026-07-15T01:42:00Z")
            );
            area.delete(2L);

            given(areaRepository.findById(areaId)).willReturn(Optional.of(area));

            assertThatThrownBy(
                    () -> areaService.deleteArea(
                            userId,
                            areaId
                    )).isInstanceOf(ItsHereException.class)
                    .hasMessage(ErrorCode.AREA_ALREADY_DELETED.getMessage());
        }
    }
}