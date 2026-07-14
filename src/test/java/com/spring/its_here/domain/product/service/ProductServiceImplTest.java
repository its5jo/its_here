package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.command.ProductUpdateCommand;
import com.spring.its_here.domain.product.dto.request.ProductSearchCondition;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductCursorResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductUpdateResponseDto;
import com.spring.its_here.domain.product.entity.Product;
import com.spring.its_here.domain.product.enums.ProductSortCriteria;
import com.spring.its_here.domain.product.enums.ProductSortDirection;
import com.spring.its_here.domain.product.repository.ProductRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.infrastructure.storage.ImageStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Nested
    @DisplayName("상품 생성")
    class CreateProductTest {


        @Test
        @DisplayName("가게 소유자인 OWNER는 상품을 생성할 수 있다")
        void createProduct_success() {

            // given
            Long loginUserId = 1L;
            UUID storeId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            UserEntity user = mock(UserEntity.class);

            Store store = mock(Store.class);
            Product savedProduct = mock(Product.class);

            ProductCreateCommand command = new ProductCreateCommand(
                    storeId,
                    "아메리카노",
                    "고소한 커피",
                    4_000,
                    false,
                    null
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(user));
            when(user.getRole()).thenReturn(UserRole.OWNER);

            when(storeRepository.findById(storeId))
                    .thenReturn(Optional.of(store));
            when(store.getUser()).thenReturn(user);
            when(user.getId()).thenReturn(loginUserId);
            when(store.getId()).thenReturn(storeId);
            when(productRepository.save(any(Product.class)))
                    .thenReturn(savedProduct);
            when(savedProduct.getId()).thenReturn(productId);

            // when
            ProductCreateResponseDto response =
                    productService.createProduct(command, loginUserId);

            // then
            assertThat(response.productId()).isEqualTo(productId);

            ArgumentCaptor<Product> captor =
                    ArgumentCaptor.forClass(Product.class);

            verify(productRepository).save(captor.capture());

            Product product = captor.getValue();

            assertThat(product.getName()).isEqualTo("아메리카노");
            assertThat(product.getDescription()).isEqualTo("고소한 커피");
            assertThat(product.getPrice()).isEqualTo(4000);
            assertThat(product.isHasHidden()).isFalse();
            assertThat(product.getImageUrl()).isNull();
            assertThat(product.getStore()).isSameAs(store);

            verify(userRepository).findById(loginUserId);
            verify(storeRepository).findById(storeId);
            verify(imageStorage, never()).store(any());
        }

        @Test
        @DisplayName("OWNER가 아니면 상품을 생성할 수 없다")
        void createProduct_fail_notOwner() {
            // given
            Long loginUserId = 1L;
            UUID storeId = UUID.randomUUID();

            ProductCreateCommand command = new ProductCreateCommand(
                    storeId,
                    "아메리카노",
                    "고소한 커피",
                    4000,
                    false,
                    null
            );

            UserEntity user = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(user));
            when(user.getRole()).thenReturn(UserRole.CUSTOMER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.createProduct(command, loginUserId)
            );

            assertThat(exception.getMessage())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN.getMessage());

            verify(storeRepository, never()).findById(any());
            verify(productRepository, never()).save(any());
            verify(imageStorage, never()).store(any());
        }
    }

    @Nested
    @DisplayName("상품 단일 조회")
    class GetProductTest {

        @Test

        @DisplayName("상품 ID로 상품을 조회할 수 있다")
        void getProduct_success() {

            // given
            UUID productId = UUID.randomUUID();
            Product product = mock(Product.class);
            UUID imageUuid = UUID.randomUUID();
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(product.getName()).thenReturn("아메리카노");
            when(product.getDescription()).thenReturn("고소한 커피");
            when(product.isHasHidden()).thenReturn(false);
            when(product.getPrice()).thenReturn(4_000);
            when(product.getImageUrl()).thenReturn("/images/" + imageUuid + ".jpg");

            // when
            ProductResponseDto response =
                    productService.getProduct(productId);

            // then
            assertThat(response.name()).isEqualTo("아메리카노");
            assertThat(response.description()).isEqualTo("고소한 커피");
            assertThat(response.hasHidden()).isFalse();
            assertThat(response.price()).isEqualTo(4_000);
            assertThat(response.imageUrl())
                    .isEqualTo("/images/" + imageUuid + ".jpg");

            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
        void getProduct_fail_notFound() {
            // given
            UUID productId = UUID.randomUUID();

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.getProduct(productId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

            verify(productRepository).findById(productId);
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProductTest {

        @Test
        @DisplayName("가게 소유자인 OWNER는 상품을 삭제할 수 있다")
        void deleteProduct_success() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();
            UserEntity owner = mock(UserEntity.class);
            Product product = mock(Product.class);
            Store store = mock(Store.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(owner.getRole()).thenReturn(UserRole.OWNER);
            when(product.getStore()).thenReturn(store);
            when(store.getUser()).thenReturn(owner);
            when(owner.getId()).thenReturn(loginUserId);

            // when
            productService.deleteProduct(productId, loginUserId);

            // then
            verify(product).delete(loginUserId);
        }

        @Test
        @DisplayName("OWNER가 아니면 상품을 삭제할 수 없다")
        void deleteProduct_fail_notOwner() {

            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity user = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(user));
            when(user.getRole()).thenReturn(UserRole.CUSTOMER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.deleteProduct(productId, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(userRepository).findById(loginUserId);
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("존재하지 않는 상품이면 예외가 발생한다")
        void deleteProduct_fail_productNotFound() {

            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity owner = mock(UserEntity.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(owner.getRole()).thenReturn(UserRole.OWNER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.deleteProduct(productId, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("상품 소유자가 아니면 삭제할 수 없다")
        void deleteProduct_fail_notProductOwner() {

            // given
            Long loginUserId = 1L;
            Long ownerId = 2L;
            UUID productId = UUID.randomUUID();

            UserEntity loginUser = mock(UserEntity.class);
            UserEntity owner = mock(UserEntity.class);
            Product product = mock(Product.class);
            Store store = mock(Store.class);

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(loginUser));
            when(loginUser.getRole()).thenReturn(UserRole.OWNER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(product.getStore()).thenReturn(store);
            when(store.getUser()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.deleteProduct(productId, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);
            verify(product, never()).delete(anyLong());
        }

    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProductTest {

        @Test
        @DisplayName("가게 소유자인 OWNER는 상품을 수정할 수 있다")
        void updateProduct_success() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity owner = mock(UserEntity.class);
            Store store = mock(Store.class);
            Product product = mock(Product.class);

            ProductUpdateCommand command = new ProductUpdateCommand(
                    productId,
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    "/images/latte.jpg"
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(owner.getRole()).thenReturn(UserRole.OWNER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(product.getStore()).thenReturn(store);
            when(store.getUser()).thenReturn(owner);
            when(owner.getId()).thenReturn(loginUserId);
            when(product.getId()).thenReturn(productId);

            // when
            ProductUpdateResponseDto response =
                    productService.updateProduct(command, loginUserId);

            // then
            assertThat(response.productId()).isEqualTo(productId);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);

            verify(product).update(
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    "/images/latte.jpg"
            );
        }

        @Test
        @DisplayName("존재하지 않는 사용자는 상품을 수정할 수 없다")
        void updateProduct_fail_userNotFound() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            ProductUpdateCommand command = new ProductUpdateCommand(
                    productId,
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    null
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.updateProduct(command, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository).findById(loginUserId);
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("OWNER가 아니면 상품을 수정할 수 없다")
        void updateProduct_fail_notOwner() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity customer = mock(UserEntity.class);

            ProductUpdateCommand command = new ProductUpdateCommand(
                    productId,
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    null
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(customer));
            when(customer.getRole()).thenReturn(UserRole.CUSTOMER);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.updateProduct(command, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(userRepository).findById(loginUserId);
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("존재하지 않는 상품이면 예외가 발생한다")
        void updateProduct_fail_productNotFound() {
            // given
            Long loginUserId = 1L;
            UUID productId = UUID.randomUUID();

            UserEntity owner = mock(UserEntity.class);

            ProductUpdateCommand command = new ProductUpdateCommand(
                    productId,
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    null
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(owner));
            when(owner.getRole()).thenReturn(UserRole.OWNER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.updateProduct(command, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("상품이 속한 가게의 소유자가 아니면 상품을 수정할 수 없다")
        void updateProduct_fail_notStoreOwner() {
            // given
            Long loginUserId = 1L;
            Long storeOwnerId = 2L;
            UUID productId = UUID.randomUUID();

            UserEntity loginUser = mock(UserEntity.class);
            UserEntity storeOwner = mock(UserEntity.class);
            Store store = mock(Store.class);
            Product product = mock(Product.class);

            ProductUpdateCommand command = new ProductUpdateCommand(
                    productId,
                    "카페라떼",
                    "고소한 우유가 들어간 커피",
                    false,
                    5_000,
                    null
            );

            when(userRepository.findById(loginUserId))
                    .thenReturn(Optional.of(loginUser));
            when(loginUser.getRole()).thenReturn(UserRole.OWNER);

            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));
            when(product.getStore()).thenReturn(store);
            when(store.getUser()).thenReturn(storeOwner);
            when(storeOwner.getId()).thenReturn(storeOwnerId);

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.updateProduct(command, loginUserId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AUTH_FORBIDDEN);

            verify(userRepository).findById(loginUserId);
            verify(productRepository).findById(productId);

            verify(product, never()).update(
                    anyString(),
                    anyString(),
                    anyBoolean(),
                    anyInt(),
                    any()
            );
        }
    }

    @Nested
    @DisplayName("가게 상품 목록 조회")
    class SearchStoreProductsTest {

        @Test
        @DisplayName("가게의 상품 목록을 커서 기반으로 조회할 수 있다")
        void searchStoreProducts_success() {
            // given
            UUID storeId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2026-07-14T10:00:00Z");

            Store store = mock(Store.class);
            Product product = mock(Product.class);

            @SuppressWarnings("unchecked")
            Slice<Product> productSlice = mock(Slice.class);

            ProductSearchCondition condition = new ProductSearchCondition(
                    ProductSortCriteria.CREATED_AT,
                    ProductSortDirection.DESCENDING,
                    null,
                    null,
                    10
            );

            when(storeRepository.findById(storeId))
                    .thenReturn(Optional.of(store));

            when(productRepository.searchProductsByCursor(
                    eq(storeId),
                    isNull(),
                    isNull(),
                    eq("DESCENDING"),
                    any(Pageable.class)
            )).thenReturn(productSlice);

            when(productSlice.getContent())
                    .thenReturn(List.of(product));
            when(productSlice.hasNext())
                    .thenReturn(true);

            when(product.getId()).thenReturn(productId);
            when(product.getCreatedAt()).thenReturn(createdAt);
            when(product.getName()).thenReturn("아메리카노");
            when(product.getDescription()).thenReturn("고소한 커피");
            when(product.isHasHidden()).thenReturn(false);
            when(product.getPrice()).thenReturn(4_000);
            when(product.getImageUrl()).thenReturn("/images/americano.jpg");

            // when
            ProductCursorResponseDto response =
                    productService.searchStoreProducts(condition, storeId);

            // then
            assertThat(response.content()).hasSize(1);

            assertThat(response.content().get(0).name())
                    .isEqualTo("아메리카노");
            assertThat(response.content().get(0).description())
                    .isEqualTo("고소한 커피");
            assertThat(response.content().get(0).hasHidden())
                    .isFalse();
            assertThat(response.content().get(0).price())
                    .isEqualTo(4_000);
            assertThat(response.content().get(0).imageUrl())
                    .isEqualTo("/images/americano.jpg");

            assertThat(response.pageInfo().paginationType())
                    .isEqualTo("CURSOR");
            assertThat(response.pageInfo().hasNext())
                    .isTrue();
            assertThat(response.pageInfo().nextCursor())
                    .isEqualTo(createdAt.toString());
            assertThat(response.pageInfo().nextIdAfter())
                    .isEqualTo(productId);
            assertThat(response.pageInfo().sortBy())
                    .isEqualTo("createdAt");
            assertThat(response.pageInfo().sortDirection())
                    .isEqualTo(ProductSortDirection.DESCENDING);

            verify(storeRepository).findById(storeId);

            verify(productRepository).searchProductsByCursor(
                    eq(storeId),
                    isNull(),
                    isNull(),
                    eq("DESCENDING"),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("다음 상품이 없으면 다음 커서 정보를 반환하지 않는다")
        void searchStoreProducts_success_noNextPage() {
            // given
            UUID storeId = UUID.randomUUID();

            Store store = mock(Store.class);
            Product product = mock(Product.class);

            @SuppressWarnings("unchecked")
            Slice<Product> productSlice = mock(Slice.class);

            ProductSearchCondition condition = new ProductSearchCondition(
                    ProductSortCriteria.CREATED_AT,
                    ProductSortDirection.DESCENDING,
                    null,
                    null,
                    10
            );

            when(storeRepository.findById(storeId))
                    .thenReturn(Optional.of(store));

            when(productRepository.searchProductsByCursor(
                    eq(storeId),
                    isNull(),
                    isNull(),
                    eq("DESCENDING"),
                    any(Pageable.class)
            )).thenReturn(productSlice);

            when(productSlice.getContent())
                    .thenReturn(List.of(product));
            when(productSlice.hasNext())
                    .thenReturn(false);

            when(product.getName()).thenReturn("아메리카노");
            when(product.getDescription()).thenReturn("고소한 커피");
            when(product.getPrice()).thenReturn(4_000);

            // when
            ProductCursorResponseDto response =
                    productService.searchStoreProducts(condition, storeId);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.pageInfo().hasNext()).isFalse();
            assertThat(response.pageInfo().nextCursor()).isNull();
            assertThat(response.pageInfo().nextIdAfter()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 가게의 상품 목록을 조회하면 예외가 발생한다")
        void searchStoreProducts_fail_storeNotFound() {
            // given
            UUID storeId = UUID.randomUUID();

            ProductSearchCondition condition = new ProductSearchCondition(
                    ProductSortCriteria.CREATED_AT,
                    ProductSortDirection.DESCENDING,
                    null,
                    null,
                    10
            );

            when(storeRepository.findById(storeId))
                    .thenReturn(Optional.empty());

            // when & then
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> productService.searchStoreProducts(condition, storeId)
            );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_FOUND);

            verify(storeRepository).findById(storeId);

            verify(productRepository, never()).searchProductsByCursor(
                    any(),
                    any(),
                    any(),
                    anyString(),
                    any(Pageable.class)
            );
        }
    }

}