package com.spring.its_here.domain.product.service;

import com.spring.its_here.domain.product.dto.command.ProductCreateCommand;
import com.spring.its_here.domain.product.dto.response.ProductCreateResponseDto;
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
    class GetProductTest {
    }
}