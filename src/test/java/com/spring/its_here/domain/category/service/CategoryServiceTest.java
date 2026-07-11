package com.spring.its_here.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.its_here.domain.category.dto.request.CategoryCreateRequestDto;
import com.spring.its_here.domain.category.dto.response.CategoryCreateResponseDto;
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
import org.springframework.test.util.ReflectionTestUtils;

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
    @DisplayName("카테고리 생성 API 테스트")
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
        void createCategory_success() {

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
                    categoryService.createCategory(userDetails, request);

            // then
            ArgumentCaptor<Category> categoryCaptor =
                    ArgumentCaptor.forClass(Category.class);

            verify(categoryRepository)
                    .save(categoryCaptor.capture());

            Category savedCategory =
                    categoryCaptor.getValue();

            assertThat(savedCategory.getName()).isEqualTo(request.name());
            assertThat(savedCategory.isHasHidden()).isEqualTo(request.hasHidden());
            assertThat(savedCategory.getCreatedBy()).isEqualTo(userDetails.getUserId());
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
                    () -> categoryService.createCategory(userDetails, request)
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
}