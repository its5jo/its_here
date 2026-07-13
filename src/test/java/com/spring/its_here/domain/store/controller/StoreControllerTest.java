package com.spring.its_here.domain.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.service.StoreService;
import com.spring.its_here.global.advice.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @InjectMocks
    private StoreController storeController;

    @Mock
    private StoreService storeService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(storeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("가게 등록")
    class CreateStore {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "교촌치킨 강남점",
                            "서울 강남구",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            StoreCreateResponseDto responseDto =
                    new StoreCreateResponseDto(storeId);

            // Service가 정상적으로 응답한다고 가정
            given(storeService.createStore(any(), any()))
                    .willReturn(responseDto);

            // when & then
            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message")
                            .value("가게 등록 성공"))
                    .andExpect(jsonPath("$.code")
                            .value("SUCCESS"))
                    .andExpect(jsonPath("$.data.storeId")
                            .value(storeId.toString()));
        }

        @Test
        @DisplayName("가게 이름 누락")
        void name_blank() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "",
                            "서울 강남구",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(storeService, never())
                    .createStore(any(), any());
        }

        @Test
        @DisplayName("가게 이름 30자 초과")
        void name_too_long() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "가".repeat(31),
                            "서울 강남구",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(storeService, never())
                    .createStore(any(), any());
        }

        @Test
        @DisplayName("가게 이름 특수문자")
        void invalid_name() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "교촌@@치킨 강남점",
                            "서울 강남구",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(storeService, never())
                    .createStore(any(), any());
        }

        @Test
        @DisplayName("주소 누락")
        void address_blank() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "교촌치킨 강남점",
                            "",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(storeService, never())
                    .createStore(any(), any());
        }

        @Test
        @DisplayName("시간이 잘못된 형식")
        void fail_invalid_open_time() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            // HTTP 요청 body를 직접 만들어서 테스트
            String requestJson = """
            {
                "name": "교촌치킨 강남점",
                "address": "서울 강남구",
                "hasOpen": true,
                "areaId": "%s",
                "categoryId": "%s",
                "openAt": "25:80",
                "closedAt": "22:00"
            }
            """.formatted(areaId, categoryId);

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andExpect(status().isBadRequest());

            verify(storeService, never())
                    .createStore(any(), any());
        }

        @Test
        @DisplayName("시간이 HH:mm 형식")
        void success_open_time() throws Exception {

            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            String requestJson = """
            {
                "name": "교촌치킨 강남점",
                "address": "서울 강남구",
                "hasOpen": true,
                "areaId": "%s",
                "categoryId": "%s",
                "openAt": "09:20",
                "closedAt": "22:00"
            }
            """.formatted(areaId, categoryId);

            StoreCreateResponseDto responseDto =
                    new StoreCreateResponseDto(storeId);

            given(storeService.createStore(any(), any()))
                    .willReturn(responseDto);

            mockMvc.perform(
                            post("/api/stores")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.storeId")
                            .value(storeId.toString()));
            verify(storeService)
                    .createStore(any(), any());
        }
    }

    @Nested
    @DisplayName("가게 조회")
    class getStore {

        @Test
        @DisplayName("가게 단건 조회 성공")
        void getStore_success() throws Exception {

            // given
            UUID storeId = UUID.randomUUID();

            StoreGetOneResponseDto responseDto =
                    new StoreGetOneResponseDto(
                            "교촌치킨 강남점",
                            "서울 강남구",
                            "역삼동",
                            "치킨",
                            4.5,
                            true,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            given(storeService.getOneStore(any(), eq(storeId)))
                    .willReturn(responseDto);

            // when & then
            mockMvc.perform(
                            get("/api/stores/{storeId}", storeId)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message")
                            .value("가게 조회 성공"))
                    .andExpect(jsonPath("$.code")
                            .value("SUCCESS"))
                    .andExpect(jsonPath("$.data.name")
                            .value("교촌치킨 강남점"))
                    .andExpect(jsonPath("$.data.address")
                            .value("서울 강남구"))
                    .andExpect(jsonPath("$.data.category")
                            .value("치킨"))
                    .andExpect(jsonPath("$.data.area")
                            .value("역삼동"))
                    .andExpect(jsonPath("$.data.rating")
                            .value(4.5))
                    .andExpect(jsonPath("$.data.hasOpen")
                            .value(true))
                    .andExpect(jsonPath("$.data.openAt")
                            .value("09:00"))
                    .andExpect(jsonPath("$.data.closedAt")
                            .value("22:00"));
        }

        @Test
        @DisplayName("가게 목록 조회 성공")
        void getAllStores_success() throws Exception {

            // given
            StoreGetAllResponseDto dto =
                    new StoreGetAllResponseDto(
                            UUID.randomUUID(),
                            "교촌치킨 강남점",
                            "치킨",
                            "서울 강남구",
                            "역삼동",
                            4.5,
                            true
                    );

            Pageable pageable =
                    PageRequest.of(
                            0,
                            10,
                            Sort.by("createdAt").ascending()
                    );

            Page<StoreGetAllResponseDto> page =
                    new PageImpl<>(
                            List.of(dto),
                            pageable,
                            1
                    );

            given(storeService.getAllStores(any(), any(), any()))
                    .willReturn(page);

            // when & then
            mockMvc.perform(
                            get("/api/stores")
                                    .param("page", "0")
                                    .param("size", "10")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message")
                            .value("가게 목록 조회 성공"))
                    .andExpect(jsonPath("$.code")
                            .value("SUCCESS"))

                    .andExpect(jsonPath("$.data.content.length()")
                            .value(1))
                    .andExpect(jsonPath("$.data.content[0].name")
                            .value("교촌치킨 강남점"))
                    .andExpect(jsonPath("$.data.content[0].category")
                            .value("치킨"))
                    .andExpect(jsonPath("$.data.content[0].address")
                            .value("서울 강남구"))
                    .andExpect(jsonPath("$.data.content[0].area")
                            .value("역삼동"))
                    .andExpect(jsonPath("$.data.content[0].rating")
                            .value(4.5))
                    .andExpect(jsonPath("$.data.content[0].hasOpen")
                            .value(true))

                    .andExpect(jsonPath("$.data.pageInfo.paginationType")
                            .value("OFFSET"))
                    .andExpect(jsonPath("$.data.pageInfo.hasNext")
                            .value(false))
                    .andExpect(jsonPath("$.data.pageInfo.totalCount")
                            .value(1))
                    .andExpect(jsonPath("$.data.pageInfo.sortBy")
                            .value("createdAt"))
                    .andExpect(jsonPath("$.data.pageInfo.sortDirection")
                            .value("ASC"));
        }
    }
}
