package com.spring.its_here;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.mapper.CategoryMapper;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.category.service.CategoryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Nested
    @DisplayName("카테고리 생성 API 테스트")
    class CreateCategoryTest {

        @Test
        @DisplayName("카테고리 생성 성공")
        void createCategory_success() {

            // given
            CategoryCreateRequestDto request =
                    new CategoryCreateRequestDto(
                            "한식",
                            false
                    );

            Category category =
                    new Category(
                            "한식",
                            false
                    );

            when(categoryMapper.toEntity(request))
                    .thenReturn(category);

            when(categoryRepository.save(any(Category.class)))
                    .thenAnswer(invocation -> {

                        Category savedCategory =
                                invocation.getArgument(0);

                        ReflectionTestUtils.setField(
                                savedCategory,
                                "id",
                                UUID.randomUUID()
                        );

                        return savedCategory;
                    });

            when(categoryMapper.toCreateResponseDto(any(Category.class)))
                    .thenAnswer(invocation -> {

                        Category savedCategory =
                                invocation.getArgument(0);

                        return new CategoryCreateResponseDto(
                                savedCategory.getId()
                        );
                    });

            // when
            CategoryCreateResponseDto response =
                    categoryService.createCategory(request);

            // then
            assertThat(response.categoryId())
                    .isNotNull();

            ArgumentCaptor<Category> saveCaptor =
                    ArgumentCaptor.forClass(Category.class);

            verify(categoryRepository)
                    .save(saveCaptor.capture());

            Category savedCategory =
                    saveCaptor.getValue();

            assertThat(savedCategory.getName())
                    .isEqualTo("한식");

            assertThat(savedCategory.isHasHidden())
                    .isFalse();

            assertThat(savedCategory.isHasDeleted())
                    .isFalse();

            verify(categoryMapper)
                    .toEntity(request);

            ArgumentCaptor<Category> responseCaptor =
                    ArgumentCaptor.forClass(Category.class);

            verify(categoryMapper)
                    .toCreateResponseDto(responseCaptor.capture());

            assertThat(responseCaptor.getValue())
                    .isEqualTo(savedCategory);
        }

        @Test
        @DisplayName("카테고리 저장 중 예외 발생")
        void createCategory_save_fail() {

            // given
            CategoryCreateRequestDto request =
                    new CategoryCreateRequestDto(
                            "한식",
                            false
                    );

            Category category =
                    new Category(
                            "한식",
                            false
                    );

            when(categoryMapper.toEntity(request))
                    .thenReturn(category);

            when(categoryRepository.save(any(Category.class)))
                    .thenThrow(new RuntimeException());

            // when & then
            assertThatThrownBy(() ->
                    categoryService.createCategory(request)
            )
                    .isInstanceOf(RuntimeException.class);

            verify(categoryMapper)
                    .toEntity(request);

            verify(categoryRepository)
                    .save(category);

            // 저장 실패 시 응답 변환 X
            verify(categoryMapper, never())
                    .toCreateResponseDto(any());
        }
    }
}