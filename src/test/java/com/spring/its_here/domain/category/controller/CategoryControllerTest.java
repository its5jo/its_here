package com.spring.its_here.domain.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.service.CategoryService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(categoryController) // Controller만 테스트 환경에 올림
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("카테고리 등록")
    class CreateStore {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            // given
            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto = new CategoryCreateRequestDto("야식", false);

            CategoryCreateResponseDto responseDto =
                    new CategoryCreateResponseDto(categoryId);

            // Service가 정상적으로 응답한다고 가정
            given(categoryService.createCategory(any(), any()))
                    .willReturn(responseDto);

            // when & then
            mockMvc.perform(
                            post("/api/categories")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message")
                            .value("카테고리 등록 성공"))
                    .andExpect(jsonPath("$.code")
                            .value("SUCCESS"))
                    .andExpect(jsonPath("$.data.categoryId")
                            .value(categoryId.toString()));
        }

        @Test
        @DisplayName("카테고리 이름 누락")
        void name_blank() throws Exception {

            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto = new CategoryCreateRequestDto("", false);

            mockMvc.perform(
                            post("/api/categories")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(categoryService, never())
                    .createCategory(any(), any());
        }

        @Test
        @DisplayName("카테고리 이름 30자 초과")
        void name_too_long() throws Exception {

            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto =
                    new CategoryCreateRequestDto("가".repeat(31), false);

            mockMvc.perform(
                            post("/api/categories")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(categoryService, never())
                    .createCategory(any(), any());
        }

        @Test
        @DisplayName("카테고리 이름에 한글 말고 다른게 존재")
        void name_contains_english() throws Exception {

            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto =
                    new CategoryCreateRequestDto("1카테gory", false);

            mockMvc.perform(
                            post("/api/categories")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(categoryService, never())
                    .createCategory(any(), any());
        }

        @Test
        @DisplayName("카테고리 숨김 여부 누락")
        void hasHidden_blank() throws Exception {

            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto =
                    new CategoryCreateRequestDto("일식", null);

            mockMvc.perform(
                            post("/api/categories")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(requestDto))
                    )
                    .andExpect(status().isBadRequest());
            verify(categoryService, never())
                    .createCategory(any(), any());
        }

    }

}