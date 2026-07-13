package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
import com.spring.its_here.domain.product.dto.response.ProductResponseDto;
import com.spring.its_here.domain.product.entity.Product;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

            given(userRepository.findById(loginUserId))
                    .willReturn(Optional.of(owner));
            given(productRepository.findById(productId))
                    .willReturn(Optional.of(product));
            given(owner.getRole()).willReturn(UserRole.OWNER);
            given(product.getStore()).willReturn(store);
            given(store.getUser()).willReturn(owner);
            given(owner.getId()).willReturn(loginUserId);

            // when
            productService.deleteProduct(productId, loginUserId);

            // then
            verify(product).delete(loginUserId);
        }

    }
}