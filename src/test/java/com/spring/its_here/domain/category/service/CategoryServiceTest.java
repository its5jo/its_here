package com.spring.its_here.domain.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryGetAllRequestDto;
import com.spring.its_here.domain.category.dto.request.CategoryUpdateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetAllResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryGetOneResponseDto;
import com.spring.its_here.domain.category.dto.response.CategoryUpdateResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;

import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UserEntity user;
    private CustomUserDetails userDetails;

    @Nested
    @DisplayName("카테고리 생성")
    class CreateCategoryTest {

        @BeforeEach
        void setUp() {
            user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            userDetails = new CustomUserDetails(user);
        }

        @Test
        @DisplayName("카테고리 생성 성공")
        void success() {

            // given
            CategoryCreateRequestDto request =
                    new CategoryCreateRequestDto(
                            "한식",
                            false
                    );

            UUID categoryId = UUID.randomUUID();

            // save()가 호출되면 ID를 넣어서 반환
            when(categoryRepository.save(any(Category.class)))
                    .thenAnswer(invocation -> {
                        Category saved = invocation.getArgument(0);
                        ReflectionTestUtils.setField(saved, "id", categoryId);
                        return saved;
                    });

            // when
            CategoryCreateResponseDto response =
                    categoryService.createCategory(request);

            // then
            ArgumentCaptor<Category> categoryCaptor =
                    ArgumentCaptor.forClass(Category.class);

            verify(categoryRepository)
                    .save(categoryCaptor.capture());

            Category savedCategory =
                    categoryCaptor.getValue();

            assertThat(savedCategory.getName()).isEqualTo(request.name());
            assertThat(savedCategory.isHasHidden()).isEqualTo(request.hasHidden());
            assertThat(savedCategory.getDeletedAt()).isNull();

            assertThat(response.categoryId()).isEqualTo(savedCategory.getId());
        }

        @Test
        @DisplayName("카테고리 생성 실패 - 중복된 name이면 예외가 발생")
        void createCategory_duplicate_name() {

            // given
            CategoryCreateRequestDto request =
                    new CategoryCreateRequestDto(
                            "한식",
                            false
                    );

            when(categoryRepository.existsByNameAndDeletedAtIsNull(request.name()))
                    .thenReturn(true);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> categoryService.createCategory(request)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NAME_DUPLICATE);

            verify(categoryRepository)
                    .existsByNameAndDeletedAtIsNull(request.name());

            verify(categoryRepository, never())
                    .save(any(Category.class));
        }

    }

    @Nested
    @DisplayName("카테고리 단건 조회")
    class GetOneCategory {

        @Test
        @DisplayName("성공")
        void success() {

            // given
            UUID categoryId = UUID.randomUUID();

            Category category = Category.createCategory("야식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            // when
            CategoryGetOneResponseDto responseDto =
                    categoryService.getOneCategory(categoryId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(category.getName());
            assertThat(responseDto.hasHidden()).isEqualTo(category.isHasHidden());

        }

        @Test
        @DisplayName("없거나 삭제된 카테고리")
        void not_exits_or_deleted() {

            // given
            UUID categoryId = UUID.randomUUID();

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> categoryService.getOneCategory(categoryId)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

        }

    }

    @Nested
    @DisplayName("카테고리 목록 조회")
    class getAllCategories {

        @Test
        @DisplayName("성공")
        void success() {

            // given
            CategoryGetAllRequestDto requestDto =
                    new CategoryGetAllRequestDto("식", false);

            Pageable pageable =
                    PageRequest.of(0, 10, Sort.by("createdAt").descending());

            CategoryGetAllResponseDto dto1 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "야식", false);

            CategoryGetAllResponseDto dto2 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "한식", false);

            Page<CategoryGetAllResponseDto> dtoList =
                    new PageImpl<>(List.of(dto1, dto2), pageable, 2);

            when(categoryRepository.getAllCategories(
                    eq(requestDto.name()),
                    eq(requestDto.hasHidden()),
                    any(Pageable.class)
            )).thenReturn(dtoList);

            // when
            Page<CategoryGetAllResponseDto> responseDto =
                    categoryService.getAllCategories(requestDto, pageable);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getContent()).hasSize(2);
            assertThat(responseDto.getContent().get(0).name()).isEqualTo("야식");
            assertThat(responseDto.getContent().get(1).name()).isEqualTo("한식");
            assertThat(responseDto.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("size가 10,30,50이 아닌 경우")
        void success_with_page_size_change() {

            // given
            CategoryGetAllRequestDto requestDto =
                    new CategoryGetAllRequestDto("한식", false);

            Pageable pageable =
                    PageRequest.of(0, 20, Sort.by("createdAt").descending());

            CategoryGetAllResponseDto dto1 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "야식", false);

            CategoryGetAllResponseDto dto2 =
                    new CategoryGetAllResponseDto(UUID.randomUUID(), "한식", false);

            Page<CategoryGetAllResponseDto> dtoList =
                    new PageImpl<>(List.of(dto2), pageable, 1);

            when(categoryRepository.getAllCategories(
                    eq(requestDto.name()),
                    eq(requestDto.hasHidden()),
                    any(Pageable.class)
            )).thenReturn(dtoList);

            // when
            Page<CategoryGetAllResponseDto> responseDto =
                    categoryService.getAllCategories(requestDto, pageable);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getContent()).hasSize(1);
            assertThat(responseDto.getContent().get(0).name()).isEqualTo("한식");
            assertThat(responseDto.getTotalElements()).isEqualTo(1);
        }

    }

    @Nested
    @DisplayName("카테고리 수정")
    class UpdateCategory {

        @Test
        @DisplayName("성공")
        void success() {

            // given
            UUID categoryId = UUID.randomUUID();

            Category category =
                    Category.createCategory("한식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            CategoryUpdateRequestDto request =
                    new CategoryUpdateRequestDto("양식", true);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            when(categoryRepository.existsByNameAndIdNotAndDeletedAtIsNull(request.name(), categoryId))
                    .thenReturn(false);

            // when
            CategoryUpdateResponseDto response =
                    categoryService.updateCategory(categoryId, request);

            // then
            assertThat(response.name()).isEqualTo("양식");
            assertThat(response.hasHidden()).isTrue();

            assertThat(category.getName()).isEqualTo("양식");
            assertThat(category.isHasHidden()).isTrue();

            verify(categoryRepository)
                    .findByIdAndDeletedAtIsNull(categoryId);

            verify(categoryRepository)
                    .existsByNameAndIdNotAndDeletedAtIsNull(request.name(), categoryId);
        }

        @Test
        @DisplayName("없거나 삭제된 카테고리")
        void fail_not_found() {

            // given
            UUID categoryId = UUID.randomUUID();

            CategoryUpdateRequestDto request =
                    new CategoryUpdateRequestDto("양식", true);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> categoryService.updateCategory(categoryId, request)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
        }

        @Test
        @DisplayName("이미 존재하는 카테고리 이름")
        void fail_duplicate_name() {

            // given
            UUID categoryId = UUID.randomUUID();

            Category category =
                    Category.createCategory("한식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            CategoryUpdateRequestDto request =
                    new CategoryUpdateRequestDto("양식", true);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            when(categoryRepository.existsByNameAndIdNotAndDeletedAtIsNull(
                    request.name(), categoryId))
                    .thenReturn(true);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> categoryService.updateCategory(categoryId, request)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }
    }

}