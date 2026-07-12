package com.spring.its_here.domain.area.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.its_here.domain.area.dto.request.AreaGetAllRequestDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllItemResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetAllResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaGetOneResponseDto;
import com.spring.its_here.domain.area.dto.response.AreaPageInfoResponseDto;
import com.spring.its_here.domain.area.service.AreaService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.config.SecurityConfig;
import com.spring.its_here.global.security.CustomUserDetailsService;
import com.spring.its_here.global.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AreaController.class)
@Import(SecurityConfig.class)
class AreaControllerTest {
    UUID areaId = UUID.randomUUID();

    @MockitoBean
    AreaService areaService;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class area_create {
        @ParameterizedTest
        @ValueSource(strings = {"city", "district", "town"})
        @DisplayName("필수 필드가 빈 문자열이면 예외")
        void createArea_field_duplicate(String missingField) throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("city", "서울특별시");
            request.put("district", "강남구");
            request.put("town", "역삼동");
            request.put(missingField, "");

            mockMvc.perform(post("/api/areas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT_VALUE.getMessage()))
                    .andExpect(jsonPath("$.details." + missingField).exists());

            verifyNoMoreInteractions(areaService);
        }
    }

    @Nested
    @DisplayName("조회")
    class getArea {
        @Test
        @DisplayName("단건조회 성공")
        void getOneArea_success() throws Exception {
            AreaGetOneResponseDto areaGetOneResponseDto = new AreaGetOneResponseDto(
                    areaId,
                    "city",
                    "district",
                    "town",
                    true,
                    Instant.parse("2026-07-12T06:00:00Z")
            );
            given(areaService.getOneArea(areaId)).willReturn(areaGetOneResponseDto);
            mockMvc.perform(get("/api/areas/{areaId}", areaId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("서비스 지역 상세 조회 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.areaId").value(areaId.toString()))
                    .andExpect(jsonPath("$.data.city").value("city"))
                    .andExpect(jsonPath("$.data.district").value("district"))
                    .andExpect(jsonPath("$.data.town").value("town"))
                    .andExpect(jsonPath("$.data.hasAvailable").value(true))
                    .andExpect(jsonPath("$.data.createdAt").value("2026-07-12T06:00:00Z"));

            verify(areaService).getOneArea(areaId);
        }

        @Test
        @DisplayName("존재하지 않는 지역 조회 시 예외")
        void getOneArea_not_found() throws Exception {
            given(areaService.getOneArea(areaId)).willThrow(new ItsHereException(ErrorCode.AREA_NOT_FOUND));

            mockMvc.perform(get("/api/areas/{areaId}", areaId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(ErrorCode.AREA_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value("지역을 찾을 수 없습니다."));

            verify(areaService).getOneArea(areaId);
        }

        @Test
        @DisplayName("전체조회 성공")
        void getAllArea_success() throws Exception {
            AreaGetAllItemResponseDto areaGetAllItemResponseDto = new AreaGetAllItemResponseDto(
                    areaId,
                    "city",
                    "district",
                    "town",
                    true
            );
            AreaPageInfoResponseDto pageInfoResponseDto = new AreaPageInfoResponseDto(
                    0,
                    10,
                    1,
                    1,
                    true
            );
            AreaGetAllResponseDto areaGetAllResponseDto = new AreaGetAllResponseDto(
                    List.of(areaGetAllItemResponseDto),
                    pageInfoResponseDto
            );

            given(areaService.getAllArea(
                    any(AreaGetAllRequestDto.class),
                    any(Pageable.class)
            )).willReturn(areaGetAllResponseDto);

            mockMvc.perform(get("/api/areas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("서비스 지역 전체 조회 성공"))
                    .andExpect(jsonPath("$.code").value("SUCCESS"));

            ArgumentCaptor<AreaGetAllRequestDto> requestCaptor = ArgumentCaptor.forClass(AreaGetAllRequestDto.class);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            verify(areaService).getAllArea(
                    requestCaptor.capture(),
                    pageableCaptor.capture()
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 20, 40, 100})
        @DisplayName("조회개수가 10, 30, 50이 아니면 예외")
        void getAllArea_invalid_size(int size) throws Exception {
            mockMvc.perform(get("/api/areas")
                            .param("size", String.valueOf(size))
                            .param("sort", "createdAt, desc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.AREA_INVALID_SIZE.getCode()));

            verify(areaService, never()).getAllArea(any(), any());
        }

        @Test
        @DisplayName("정렬 기준이 createdAt이 아니면 예외")
        void getAllArea_invalid_sort() throws Exception {
            mockMvc.perform(get("/api/areas")
                            .param("size", "10")
                            .param("sort", "updatedAt, desc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(ErrorCode.AREA_INVALID_SORT_BY.getCode()));

            verify(areaService, never()).getAllArea(any(), any());
        }
    }
}