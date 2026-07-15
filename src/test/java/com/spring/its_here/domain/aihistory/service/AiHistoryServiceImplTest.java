package com.spring.its_here.domain.aihistory.service;

import com.spring.its_here.domain.aihistory.dto.request.AiHistorySearchCondition;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryCursorResponseDto;
import com.spring.its_here.domain.aihistory.dto.response.AiHistoryResponseDto;
import com.spring.its_here.domain.aihistory.entity.AiHistory;
import com.spring.its_here.domain.aihistory.enums.AiHistorySortCriteria;
import com.spring.its_here.domain.aihistory.enums.AiHistorySortDirection;
import com.spring.its_here.domain.aihistory.repository.AiHistoryRepository;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiHistoryServiceImplTest {

    @Mock
    private AiHistoryRepository aiHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AiHistoryServiceImpl aiHistoryService;

    @Nested
    @DisplayName("AI 이력 단일 조회")
    class GetAiHistoryTest {

        @Test
        @DisplayName("MANAGER는 AI 이력을 단일 조회할 수 있다")
        void getAiHistory_success_manager() {
            // given
            Long loginUserId = 1L;
            UUID aiHistoryId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2026-07-15T07:00:00Z");

            UserEntity manager = mock(UserEntity.class);
            AiHistory aiHistory = mock(AiHistory.class);
            Product product = mock(Product.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(aiHistoryRepository.findById(aiHistoryId))
                    .thenReturn(Optional.of(aiHistory));

            mockAiHistory(
                    aiHistory,
                    product,
                    aiHistoryId,
                    productId,
                    "상품 설명 프롬프트",
                    "상품 설명 응답",
                    createdAt
            );

            // when
            AiHistoryResponseDto response =
                    aiHistoryService.getAiHistory(aiHistoryId, loginUserId);

            // then
            assertThat(response.id()).isEqualTo(aiHistoryId);
            assertThat(response.productId()).isEqualTo(productId);
            assertThat(response.prompt()).isEqualTo("상품 설명 프롬프트");
            assertThat(response.response()).isEqualTo("상품 설명 응답");
            assertThat(response.createdAt()).isEqualTo(createdAt);

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository).findById(aiHistoryId);
        }

        @Test
        @DisplayName("MASTER는 AI 이력을 단일 조회할 수 있다")
        void getAiHistory_success_master() {
            // given
            Long loginUserId = 1L;
            UUID aiHistoryId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            UserEntity master = mock(UserEntity.class);
            AiHistory aiHistory = mock(AiHistory.class);
            Product product = mock(Product.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(master));
            when(master.getRole())
                    .thenReturn(UserRole.MASTER);

            when(aiHistoryRepository.findById(aiHistoryId))
                    .thenReturn(Optional.of(aiHistory));

            mockAiHistory(
                    aiHistory,
                    product,
                    aiHistoryId,
                    productId,
                    "프롬프트",
                    "응답",
                    Instant.parse("2026-07-15T07:00:00Z")
            );

            // when
            AiHistoryResponseDto response =
                    aiHistoryService.getAiHistory(aiHistoryId, loginUserId);

            // then
            assertThat(response.id()).isEqualTo(aiHistoryId);

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository).findById(aiHistoryId);
        }

        @Test
        @DisplayName("존재하지 않는 사용자는 AI 이력을 조회할 수 없다")
        void getAiHistory_fail_userNotFound() {
            // given
            Long loginUserId = 1L;
            UUID aiHistoryId = UUID.randomUUID();

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.getAiHistory(
                            aiHistoryId,
                            loginUserId
                    )
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository, never()).findById(any());
        }

        @Test
        @DisplayName("MANAGER나 MASTER가 아니면 AI 이력을 조회할 수 없다")
        void getAiHistory_fail_forbidden() {
            // given
            Long loginUserId = 1L;
            UUID aiHistoryId = UUID.randomUUID();

            UserEntity owner = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(owner.getRole())
                    .thenReturn(UserRole.OWNER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.getAiHistory(
                            aiHistoryId,
                            loginUserId
                    )
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository, never()).findById(any());
        }

        @Test
        @DisplayName("존재하지 않는 AI 이력을 조회하면 예외가 발생한다")
        void getAiHistory_fail_notFound() {
            // given
            Long loginUserId = 1L;
            UUID aiHistoryId = UUID.randomUUID();

            UserEntity manager = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(aiHistoryRepository.findById(aiHistoryId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.getAiHistory(
                            aiHistoryId,
                            loginUserId
                    )
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AI_HISTORY_NOT_FOUND);

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository).findById(aiHistoryId);
        }
    }

    @Nested
    @DisplayName("AI 이력 전체 조회")
    class GetAiHistoriesTest {

        @Test
        @DisplayName("MANAGER는 AI 이력 전체 목록을 조회할 수 있다")
        void getAiHistories_success() {
            // given
            Long loginUserId = 1L;

            UserEntity manager = mock(UserEntity.class);
            AiHistory firstHistory = mock(AiHistory.class);
            AiHistory secondHistory = mock(AiHistory.class);
            Product firstProduct = mock(Product.class);
            Product secondProduct = mock(Product.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(aiHistoryRepository.findAllWithProduct())
                    .thenReturn(List.of(firstHistory, secondHistory));

            mockAiHistory(
                    firstHistory,
                    firstProduct,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "첫 번째 프롬프트",
                    "첫 번째 응답",
                    Instant.parse("2026-07-15T07:00:00Z")
            );

            mockAiHistory(
                    secondHistory,
                    secondProduct,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "두 번째 프롬프트",
                    "두 번째 응답",
                    Instant.parse("2026-07-15T08:00:00Z")
            );

            // when
            List<AiHistoryResponseDto> responses =
                    aiHistoryService.getAiHistories(loginUserId);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).prompt())
                    .isEqualTo("첫 번째 프롬프트");
            assertThat(responses.get(1).response())
                    .isEqualTo("두 번째 응답");

            verify(userRepository).findById(loginUserId);
            verify(aiHistoryRepository).findAllWithProduct();
        }

        @Test
        @DisplayName("권한이 없으면 AI 이력 전체 목록을 조회할 수 없다")
        void getAiHistories_fail_forbidden() {
            // given
            Long loginUserId = 1L;
            UserEntity customer = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(customer));
            when(customer.getRole())
                    .thenReturn(UserRole.CUSTOMER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.getAiHistories(loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(aiHistoryRepository, never()).findAllWithProduct();
        }
    }

    @Nested
    @DisplayName("상품별 AI 이력 목록 조회")
    class SearchAiHistoriesTest {

        @Test
        @DisplayName("MANAGER는 상품별 AI 이력을 커서 기반으로 조회할 수 있다")
        void searchAiHistories_success() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();
            UUID aiHistoryId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2026-07-15T10:00:00Z");

            UserEntity manager = mock(UserEntity.class);
            Product product = mock(Product.class);
            AiHistory aiHistory = mock(AiHistory.class);

            @SuppressWarnings("unchecked")
            Slice<AiHistory> aiHistorySlice = mock(Slice.class);

            AiHistorySearchCondition condition =
                    new AiHistorySearchCondition(
                            AiHistorySortCriteria.CREATED_AT,
                            AiHistorySortDirection.DESCENDING,
                            null,
                            null,
                            10
                    );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            when(aiHistoryRepository.searchAiHistoriesByCursor(
                    eq(productId),
                    isNull(),
                    isNull(),
                    eq("DESCENDING"),
                    any(Pageable.class)
            )).thenReturn(aiHistorySlice);

            when(aiHistorySlice.getContent())
                    .thenReturn(List.of(aiHistory));
            when(aiHistorySlice.hasNext())
                    .thenReturn(true);

            mockAiHistory(
                    aiHistory,
                    product,
                    aiHistoryId,
                    productId,
                    "상품 설명 작성",
                    "고소한 풍미가 느껴지는 상품입니다.",
                    createdAt
            );

            // when
            AiHistoryCursorResponseDto response =
                    aiHistoryService.searchAiHistories(
                            condition,
                            productId,
                            loginUserId
                    );

            // then
            assertThat(response.content()).hasSize(1);

            AiHistoryResponseDto historyResponse =
                    response.content().get(0);

            assertThat(historyResponse.id()).isEqualTo(aiHistoryId);
            assertThat(historyResponse.productId()).isEqualTo(productId);
            assertThat(historyResponse.prompt()).isEqualTo("상품 설명 작성");
            assertThat(historyResponse.response())
                    .isEqualTo("고소한 풍미가 느껴지는 상품입니다.");
            assertThat(historyResponse.createdAt()).isEqualTo(createdAt);

            assertThat(response.pageInfo().paginationType())
                    .isEqualTo("CURSOR");
            assertThat(response.pageInfo().hasNext()).isTrue();
            assertThat(response.pageInfo().nextCursor())
                    .isEqualTo(createdAt.toString());
            assertThat(response.pageInfo().nextIdAfter())
                    .isEqualTo(aiHistoryId);
            assertThat(response.pageInfo().sortBy())
                    .isEqualTo("createdAt");
            assertThat(response.pageInfo().sortDirection())
                    .isEqualTo(AiHistorySortDirection.DESCENDING);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);

            verify(aiHistoryRepository)
                    .searchAiHistoriesByCursor(
                            eq(productId),
                            isNull(),
                            isNull(),
                            eq("DESCENDING"),
                            any(Pageable.class)
                    );
        }

        @Test
        @DisplayName("다음 AI 이력이 없으면 다음 커서 정보를 반환하지 않는다")
        void searchAiHistories_success_noNextPage() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity master = mock(UserEntity.class);
            Product product = mock(Product.class);
            AiHistory aiHistory = mock(AiHistory.class);

            @SuppressWarnings("unchecked")
            Slice<AiHistory> aiHistorySlice = mock(Slice.class);

            AiHistorySearchCondition condition =
                    new AiHistorySearchCondition(
                            AiHistorySortCriteria.CREATED_AT,
                            AiHistorySortDirection.DESCENDING,
                            null,
                            null,
                            10
                    );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(master));
            when(master.getRole())
                    .thenReturn(UserRole.MASTER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            when(aiHistoryRepository.searchAiHistoriesByCursor(
                    eq(productId),
                    isNull(),
                    isNull(),
                    eq("DESCENDING"),
                    any(Pageable.class)
            )).thenReturn(aiHistorySlice);

            when(aiHistorySlice.getContent())
                    .thenReturn(List.of(aiHistory));
            when(aiHistorySlice.hasNext())
                    .thenReturn(false);

            mockAiHistory(
                    aiHistory,
                    product,
                    UUID.randomUUID(),
                    productId,
                    "프롬프트",
                    "응답",
                    Instant.parse("2026-07-15T10:00:00Z")
            );

            // when
            AiHistoryCursorResponseDto response =
                    aiHistoryService.searchAiHistories(
                            condition,
                            productId,
                            loginUserId
                    );

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.pageInfo().hasNext()).isFalse();
            assertThat(response.pageInfo().nextCursor()).isNull();
            assertThat(response.pageInfo().nextIdAfter()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 상품의 AI 이력을 조회하면 예외가 발생한다")
        void searchAiHistories_fail_productNotFound() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity manager = mock(UserEntity.class);

            AiHistorySearchCondition condition =
                    new AiHistorySearchCondition(
                            AiHistorySortCriteria.CREATED_AT,
                            AiHistorySortDirection.DESCENDING,
                            null,
                            null,
                            10
                    );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.searchAiHistories(
                            condition,
                            productId,
                            loginUserId
                    )
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

            verify(productRepository).findById(productId);

            verify(aiHistoryRepository, never())
                    .searchAiHistoriesByCursor(
                            any(),
                            any(),
                            any(),
                            anyString(),
                            any(Pageable.class)
                    );
        }

        @Test
        @DisplayName("권한이 없으면 상품별 AI 이력을 조회할 수 없다")
        void searchAiHistories_fail_forbidden() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity owner = mock(UserEntity.class);

            AiHistorySearchCondition condition =
                    new AiHistorySearchCondition(
                            AiHistorySortCriteria.CREATED_AT,
                            AiHistorySortDirection.DESCENDING,
                            null,
                            null,
                            10
                    );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(owner.getRole())
                    .thenReturn(UserRole.OWNER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> aiHistoryService.searchAiHistories(
                            condition,
                            productId,
                            loginUserId
                    )
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(productRepository, never()).findById(any());
            verify(aiHistoryRepository, never())
                    .searchAiHistoriesByCursor(
                            any(),
                            any(),
                            any(),
                            anyString(),
                            any(Pageable.class)
                    );
        }

        @Test
        @DisplayName("커서와 정렬 방향을 Repository에 전달한다")
        void searchAiHistories_success_withCursor() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();
            UUID idAfter = UUID.randomUUID();
            String cursor = "2026-07-15T10:00:00Z";

            UserEntity manager = mock(UserEntity.class);
            Product product = mock(Product.class);

            @SuppressWarnings("unchecked")
            Slice<AiHistory> aiHistorySlice = mock(Slice.class);

            AiHistorySearchCondition condition =
                    new AiHistorySearchCondition(
                            AiHistorySortCriteria.CREATED_AT,
                            AiHistorySortDirection.ASCENDING,
                            cursor,
                            idAfter,
                            10
                    );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(manager));
            when(manager.getRole())
                    .thenReturn(UserRole.MANAGER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            when(aiHistoryRepository.searchAiHistoriesByCursor(
                    eq(productId),
                    eq(cursor),
                    eq(idAfter),
                    eq("ASCENDING"),
                    any(Pageable.class)
            )).thenReturn(aiHistorySlice);

            when(aiHistorySlice.getContent())
                    .thenReturn(List.of());
            when(aiHistorySlice.hasNext())
                    .thenReturn(false);

            // when
            AiHistoryCursorResponseDto response =
                    aiHistoryService.searchAiHistories(
                            condition,
                            productId,
                            loginUserId
                    );

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.pageInfo().hasNext()).isFalse();

            verify(aiHistoryRepository)
                    .searchAiHistoriesByCursor(
                            eq(productId),
                            eq(cursor),
                            eq(idAfter),
                            eq("ASCENDING"),
                            any(Pageable.class)
                    );
        }
    }

    private void mockAiHistory(
            AiHistory aiHistory,
            Product product,
            UUID aiHistoryId,
            UUID productId,
            String prompt,
            String response,
            Instant createdAt
    ) {
        when(aiHistory.getId()).thenReturn(aiHistoryId);
        when(aiHistory.getProduct()).thenReturn(product);
        when(product.getId()).thenReturn(productId);
        when(aiHistory.getPrompt()).thenReturn(prompt);
        when(aiHistory.getResponse()).thenReturn(response);
        when(aiHistory.getCreatedAt()).thenReturn(createdAt);
    }
}