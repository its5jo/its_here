package com.spring.its_here.domain.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
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

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

        mockMvc = standaloneSetup(storeController) // Controller만 테스트 환경에 올림
                .setControllerAdvice(new GlobalExceptionHandler())
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
}
