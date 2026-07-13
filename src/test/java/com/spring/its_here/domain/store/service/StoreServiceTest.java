package com.spring.its_here.domain.store.service;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
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

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AreaRepository areaRepository;

    @Nested
    @DisplayName("가게 생성")
    class CreateStore {

        @Test
        @DisplayName("성공")
        void createStore_success() {

            // given
            // 카테고리, 지역 ID 생성
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            // 가게 생성 요청 DTO 생성
            StoreCreateRequestDto requestDto =
                    new StoreCreateRequestDto(
                            "교촌치킨 역삼점",
                            "서울 강남구",
                            true,
                            areaId,
                            categoryId,
                            LocalTime.of(9, 0),
                            LocalTime.of(22, 0)
                    );

            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.OWNER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 모든 검증을 통과한다고 가정
            when(storeRepository.existsByNameAndDeletedAtIsNull(requestDto.name()))
                    .thenReturn(false);

            when(storeRepository.existsByUserIdAndDeletedAtIsNull(user.getId()))
                    .thenReturn(false);

            Category category =
                    Category.createCategory("야식", false);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            Area area = Area.create("서울특별시", "송파구", "방이동");

            when(areaRepository.findByIdAndDeletedAtIsNull(areaId))
                    .thenReturn(Optional.of(area));

            UUID storeId = UUID.randomUUID();

            // 실제 JPA처럼 저장된 객체를 반환
            when(storeRepository.save(any(Store.class)))
                    .thenAnswer(invocation -> {
                        Store saved = invocation.getArgument(0);
                        ReflectionTestUtils.setField(saved, "id", storeId);
                        return saved;
                    });

            // 서비스 호출
            StoreCreateResponseDto responseDto =
                    storeService.createStore(userDetails, requestDto);

            // 응답 Dto 검증
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.storeId()).isEqualTo(storeId);

            // Repository 호출 검증
            verify(storeRepository).existsByNameAndDeletedAtIsNull(requestDto.name());
            verify(storeRepository).existsByUserIdAndDeletedAtIsNull(user.getId());
            verify(categoryRepository).findByIdAndDeletedAtIsNull(categoryId);
            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);

            // Mock 객체의 메서드에 전달된 인자를 가져오기 위한 객체
            // forClass: 캡처할 인자의 타입을 지정
            ArgumentCaptor<Store> storeCaptor =
                    ArgumentCaptor.forClass(Store.class);

            // storeRepository.save()가 호출되었는지 검증,
            // save()에 전달된 Store 객체를 storeCaptor에 저장
            verify(storeRepository).save(storeCaptor.capture());

            // save()에 전달된 Store 객체를 가져옴
            Store savedStore = storeCaptor.getValue();

            assertThat(savedStore.getName()).isEqualTo(requestDto.name());
            assertThat(savedStore.getAddress()).isEqualTo(requestDto.address());
            assertThat(savedStore.getCategory()).isEqualTo(category);
            assertThat(savedStore.getArea()).isEqualTo(area);
            assertThat(savedStore.getUser()).isEqualTo(user);
            assertThat(savedStore.getOpenAt()).isEqualTo(requestDto.openAt());
            assertThat(savedStore.getClosedAt()).isEqualTo(requestDto.closedAt());
            assertThat(savedStore.getHasOpen()).isEqualTo(requestDto.hasOpen());
        }

        @Test
        @DisplayName("이미 이름이 같은 가게가 존재")
        void createStore_duplicate_store_name() {

            // given
            // 카테고리, 지역 ID 생성
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            // 가게 생성 요청 DTO 생성
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

            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.MASTER
            );

            // Reflection으로 PK를 넣어준다.
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 이미 같은 이름의 가게가 존재한다고 가정
            when(storeRepository.existsByNameAndDeletedAtIsNull("교촌치킨 강남점"))
                    .thenReturn(true);

            // when & then

            // DUPLICATE_STORE_NAME 예외 발생
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.createStore(userDetails, requestDto)
                    );

            // 발생한 예외의 ErrorCode가 DUPLICATE_STORE_NAME인지 검증
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NAME_DUPLICATE);

            // Store 객체가 save 되지 않았음을 검증
            verify(storeRepository, never())
                    .save(any(Store.class));

        }

        @Test
        @DisplayName("없거나 삭제된 카테고리")
        void createStore_not_exists_category() {

            // given
            // 카테고리, 지역 ID 생성
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            // 가게 생성 요청 DTO 생성
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

            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.MANAGER
            );

            // Reflection으로 PK를 넣어준다.
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 카테고리가 없거나 삭제됐다고 가정
            when(categoryRepository.findByIdAndDeletedAtIsNull(requestDto.categoryId()))
                    .thenReturn(Optional.empty());

            // when & then

            // CATEGORY_NOT_FOUND 예외 발생
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.createStore(userDetails, requestDto)
                    );

            // 발생한 예외의 ErrorCode가 CATEGORY_NOT_FOUND인지 검증
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

            // Store 객체가 save 되지 않았음을 검증
            verify(storeRepository, never())
                    .save(any(Store.class));

        }

        @Test
        @DisplayName("없거나 삭제된 지역")
        void createStore_not_exists_area() {

            // given
            // 카테고리, 지역 ID 생성
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            // 가게 생성 요청 DTO 생성
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

            // OWNER 계정이라고 가정
            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.OWNER
            );

            // Reflection으로 PK를 넣어준다.
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category =
                    Category.createCategory("야식", false);

            // 카테고리가 있다고 가정
            when(categoryRepository.findByIdAndDeletedAtIsNull(requestDto.categoryId()))
                    .thenReturn(Optional.of(category));

            // 지역이 없거나 삭제됐다고 가정
            when(areaRepository.findByIdAndDeletedAtIsNull(requestDto.areaId()))
                    .thenReturn(Optional.empty());

            // when & then

            // CATEGORY_NOT_FOUND 예외 발생
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.createStore(userDetails, requestDto)
                    );

            // 발생한 예외의 ErrorCode가 AREA_NOT_FOUND인지 검증
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.AREA_NOT_FOUND);

            // Store 객체가 save 되지 않았음을 검증
            verify(storeRepository, never())
                    .save(any(Store.class));

        }

    }

    @Nested
    @DisplayName("가게 단건 조회")
    class GetOntStore {

        @Test
        @DisplayName("성공")
        void getOneStore_success() {

            // given

            UUID storeId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("야식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            Area area = Area.create("서울특별시", "강남구", "역삼동");

            ReflectionTestUtils.setField(area, "id", areaId);

            Store store = Store.createStore(
                    "교촌치킨 역삼점",
                    "서울 강남구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            ReflectionTestUtils.setField(store, "id", storeId);

            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // when
            StoreGetOneResponseDto responseDto =
                    storeService.getOneStore(userDetails, storeId);

            // then

            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(store.getName());
            assertThat(responseDto.address()).isEqualTo(store.getAddress());
            assertThat(responseDto.category()).isEqualTo(category.getName());
            assertThat(responseDto.area()).isEqualTo(area.getTown());
            assertThat(responseDto.rating()).isEqualTo(0.0);
            assertThat(responseDto.hasOpen()).isEqualTo(store.getHasOpen());
            assertThat(responseDto.openAt()).isEqualTo(store.getOpenAt());
            assertThat(responseDto.closedAt()).isEqualTo(store.getClosedAt());

            verify(storeRepository)
                    .findByIdAndDeletedAtIsNull(storeId);

            // 추가 Repository 호출이 없는지 검증
            verifyNoMoreInteractions(
                    storeRepository,
                    categoryRepository,
                    areaRepository
            );
        }

        @Test
        @DisplayName("삭제되거나 없는 가게인 경우")
        void store_not_exists_or_deleted() {

            // given
            UUID storeId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            UserEntity storeOwner = UserEntity.create(
                    "owner1",
                    "password",
                    "사장1",
                    UserRole.OWNER
            );
            ReflectionTestUtils.setField(storeOwner, "id", 1L);

            // 다른 가게 주인
            UserEntity loginUser = UserEntity.create(
                    "owner2",
                    "password",
                    "사장2",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(loginUser, "id", 2L);

            CustomUserDetails userDetails = new CustomUserDetails(loginUser);

            Category category = Category.createCategory("야식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            Area area = Area.create("서울특별시", "강남구", "역삼동");

            ReflectionTestUtils.setField(area, "id", areaId);

            Store store = Store.createStore(
                    "교촌치킨 역삼점",
                    "서울 강남구",
                    storeOwner,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            ReflectionTestUtils.setField(store, "id", storeId);

            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.getOneStore(userDetails, storeId)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_FOUND);

            verify(storeRepository)
                    .findByIdAndDeletedAtIsNull(storeId);

        }

        @Test
        @DisplayName("다른 가게 주인인 경우")
        void not_store_owner() {

            // given
            UUID storeId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            UserEntity storeOwner = UserEntity.create(
                    "owner1",
                    "password",
                    "사장1",
                    UserRole.OWNER
            );
            ReflectionTestUtils.setField(storeOwner, "id", 1L);

            // 다른 가게 주인
            UserEntity loginUser = UserEntity.create(
                    "owner2",
                    "password",
                    "사장2",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(loginUser, "id", 2L);

            CustomUserDetails userDetails = new CustomUserDetails(loginUser);

            Category category = Category.createCategory("야식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            Area area = Area.create("서울특별시", "강남구", "역삼동");

            ReflectionTestUtils.setField(area, "id", areaId);

            Store store = Store.createStore(
                    "교촌치킨 역삼점",
                    "서울 강남구",
                    storeOwner,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            ReflectionTestUtils.setField(store, "id", storeId);

            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.getOneStore(userDetails, storeId)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_OWNED);

            verify(storeRepository)
                    .findByIdAndDeletedAtIsNull(storeId);

        }

        @Test
        @DisplayName("삭제된 지역, 카테고리는 이름 뒤에 (삭제됨) 추가")
        void deleted_area_category() {

            // given

            UUID storeId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "test1",
                    "password",
                    "닉네임",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("야식", false);

            ReflectionTestUtils.setField(category, "id", categoryId);

            category.delete(user.getId());

            Area area = Area.create("서울특별시", "강남구", "역삼동");

            ReflectionTestUtils.setField(area, "id", areaId);

            area.delete(user.getId());

            Store store = Store.createStore(
                    "교촌치킨 역삼점",
                    "서울 강남구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            ReflectionTestUtils.setField(store, "id", storeId);

            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // when
            StoreGetOneResponseDto responseDto =
                    storeService.getOneStore(userDetails, storeId);

            // then

            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(store.getName());
            assertThat(responseDto.address()).isEqualTo(store.getAddress());
            assertThat(responseDto.category()).isEqualTo(category.getName() + "(삭제됨)");
            assertThat(responseDto.area()).isEqualTo(area.getTown() + "(삭제됨)");
            assertThat(responseDto.rating()).isEqualTo(0.0);
            assertThat(responseDto.hasOpen()).isEqualTo(store.getHasOpen());
            assertThat(responseDto.openAt()).isEqualTo(store.getOpenAt());
            assertThat(responseDto.closedAt()).isEqualTo(store.getClosedAt());

            verify(storeRepository)
                    .findByIdAndDeletedAtIsNull(storeId);

            // 추가 Repository 호출이 없는지 검증
            verifyNoMoreInteractions(
                    storeRepository,
                    categoryRepository,
                    areaRepository
            );
        }
    }

    @Nested
    @DisplayName("가게 목록 조회")
    class GetAllStores {

        @Test
        @DisplayName("성공")
        void getAllStores_success() {

            // given
            Pageable pageable =
                    PageRequest.of(0, 10, Sort.by("createdAt").ascending());

            StoreGetAllResponseDto dto1 =
                    new StoreGetAllResponseDto(
                            UUID.randomUUID(),
                            "교촌치킨 강남점",
                            "치킨",
                            "서울 강남구",
                            "역삼동",
                            4.5,
                            true
                    );

            StoreGetAllResponseDto dto2 =
                    new StoreGetAllResponseDto(
                            UUID.randomUUID(),
                            "교촌치킨 선릉점",
                            "치킨",
                            "서울 강남구",
                            "삼성동",
                            3.2,
                            true
                    );

            Page<StoreGetAllResponseDto> page =
                    new PageImpl<>(List.of(dto1, dto2), pageable, 2);

            when(storeRepository.getAllStores(
                    "교촌치킨",
                    "치킨",
                    pageable
            )).thenReturn(page);

            // when
            Page<StoreGetAllResponseDto> result =
                    storeService.getAllStores("교촌치킨", "치킨", pageable);

            // then
            assertThat(result).isEqualTo(page);

            verify(storeRepository)
                    .getAllStores("교촌치킨", "치킨", pageable);
        }

        @Test
        @DisplayName("페이지 크기가 10,30,50이 아니면 10으로 변경")
        void validatePageable_change_to_10() {

            // given
            Pageable pageable =
                    PageRequest.of(
                            1,
                            20,
                            Sort.by("createdAt").descending()
                    );

            // 빈 Page 반환
            when(storeRepository.getAllStores(
                    any(),
                    any(),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            // when
            storeService.getAllStores("교촌치킨","치킨", pageable);

            // then
            ArgumentCaptor<Pageable> captor =
                    ArgumentCaptor.forClass(Pageable.class);

            verify(storeRepository)
                    .getAllStores(
                            eq("교촌치킨"),
                            eq("치킨"),
                            captor.capture()
                    );

            Pageable newPageable = captor.getValue();

            assertThat(newPageable.getPageSize()).isEqualTo(10);
            assertThat(newPageable.getPageNumber()).isEqualTo(1);
            assertThat(newPageable.getSort()).isEqualTo(pageable.getSort());
        }
    }

    @Test
    @DisplayName("페이지 크기가 50이면 그대로 사용")
    void validatePageable_size50() {

        Pageable pageable =
                PageRequest.of(0, 50);

        when(storeRepository.getAllStores(
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        storeService.getAllStores("교촌치킨","치킨", pageable);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(storeRepository)
                .getAllStores(
                        eq("교촌치킨"),
                        eq("치킨"),
                        captor.capture()
                );

        assertThat(captor.getValue().getPageSize())
                .isEqualTo(50);
    }

}