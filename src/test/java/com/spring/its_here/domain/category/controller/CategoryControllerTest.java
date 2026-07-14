package com.spring.its_here.domain.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.category.service.CategoryService;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.GlobalExceptionHandler;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.assertj.core.api.Assertions.assertThat;

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
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("카테고리 등록")
    class CreateCategory {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            // given
            UUID categoryId = UUID.randomUUID();

            CategoryCreateRequestDto requestDto = new CategoryCreateRequestDto("야식", false);

            CategoryCreateResponseDto responseDto =
                    new CategoryCreateResponseDto(categoryId);

            // Service가 정상적으로 응답한다고 가정
            given(categoryService.createCategory(any()))
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
                    .createCategory(any());
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
                    .createCategory(any());
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
                    .createCategory(any());
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
                    .createCategory(any());
        }

    }

    @Nested
    @DisplayName("카테고리 단건 조회")
    class getOneCategory {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            // given
            UUID categoryId = UUID.randomUUID();

            CategoryGetOneResponseDto responseDto =
                    new CategoryGetOneResponseDto("야식", false);

            // Service가 정상적으로 응답한다고 가정
            given(categoryService.getOneCategory(categoryId))
                    .willReturn(responseDto);

            // when & then
            mockMvc.perform(
                            get("/api/categories/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message")
                            .value("카테고리 조회 성공"))
                    .andExpect(jsonPath("$.code")
                            .value("SUCCESS"))
                    .andExpect(jsonPath("$.data.name")
                            .value("야식"))
                    .andExpect(jsonPath("$.data.hasHidden")
                            .value(false));

        }

        @Test
        @DisplayName("삭제된 카테고리 조회")
        void fail_get_deleted_category() throws Exception {

            // given
            UUID categoryId = UUID.randomUUID();

            given(categoryService.getOneCategory(categoryId))
                    .willThrow(new ItsHereException(ErrorCode.CATEGORY_NOT_FOUND));

            // when & then
            mockMvc.perform(
                            get("/api/categories/{categoryId}", categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code")
                            .value("C-002"));

        }

    }

    @Nested
    @DisplayName("카테고리 목록 조회")
    class getAllCategories {

        @Test
        @DisplayName("성공")
        void success() throws Exception {

            // given
            CategoryGetAllRequestDto requestDto = new CategoryGetAllRequestDto("식", false);

            Pageable pageable =
                    PageRequest.of(0, 10, Sort.by("createdAt").descending());

            CategoryGetAllResponseDto dto1 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "야식", false);

            CategoryGetAllResponseDto dto2 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "한식", false);

            Page<CategoryGetAllResponseDto> page =
                    new PageImpl<>(List.of(dto1, dto2), pageable, 2);

            given(categoryService.getAllCategories(any(), any()))
                    .willReturn(page);

            // when & then
            mockMvc.perform(
                            get("/api/categories")
                                    .param("name", requestDto.name())
                                    .param("hasHidden", String.valueOf(requestDto.hasHidden()))
                                    .param("page", "0")
                                    .param("size", "10")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message")
                            .value("카테고리 목록 조회 성공"))

                    .andExpect(jsonPath("$.data.content.length()")
                            .value(2))
                    .andExpect(jsonPath("$.data.content[0].name")
                            .value("야식"))
                    .andExpect(jsonPath("$.data.content[0].hasHidden")
                            .value(false))
                    .andExpect(jsonPath("$.data.content[1].name")
                            .value("한식"))
                    .andExpect(jsonPath("$.data.content[1].hasHidden")
                            .value(false))

                    .andExpect(jsonPath("$.data.pageInfo.paginationType")
                            .value("OFFSET"))
                    .andExpect(jsonPath("$.data.pageInfo.hasNext")
                            .value(false))
                    .andExpect(jsonPath("$.data.pageInfo.totalCount")
                            .value(2))
                    .andExpect(jsonPath("$.data.pageInfo.sortBy")
                            .value("createdAt"))
                    .andExpect(jsonPath("$.data.pageInfo.sortDirection")
                            .value("DESC"));
        }

        @Test
        @DisplayName("size가 10,30,50이 아닌 경우 변경 후 조회")
        void validateSizeAndUpdate() throws Exception {

            // given
            CategoryGetAllRequestDto requestDto = new CategoryGetAllRequestDto("식", false);

            Pageable newPageable =
                    PageRequest.of(0, 10, Sort.by("createdAt").descending());

            CategoryGetAllResponseDto dto1 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "야식", false);
            Page<CategoryGetAllResponseDto> page =
                    new PageImpl<>(List.of(dto1), newPageable, 1);

            given(categoryService.getAllCategories(any(), eq(newPageable)))
                    .willReturn(page);

            // when & then
            mockMvc.perform(
                            get("/api/categories")
                                    .param("name", requestDto.name())
                                    .param("hasHidden", String.valueOf(requestDto.hasHidden()))
                                    .param("page", "0")
                                    .param("size", "100")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("카테고리 목록 조회 성공"));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            verify(categoryService).getAllCategories(any(), pageableCaptor.capture());

            Pageable passedPageable = pageableCaptor.getValue();

            assertThat(passedPageable.getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("정렬 기준이 생성일시, 카테고리 이름이 아닌 경우")
        void fail_invalidSortField() throws Exception {

            // given
            CategoryGetAllRequestDto requestDto
                    = new CategoryGetAllRequestDto("식", false);

            // when & then
            mockMvc.perform(
                            get("/api/categories")
                                    .param("name", requestDto.name())
                                    .param("hasHidden", String.valueOf(requestDto.hasHidden()))
                                    .param("page", "0")
                                    .param("size", "10")
                                    .param("sort", "updatedAt,desc")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code")
                            .value("C-003"));
        }
    }

}