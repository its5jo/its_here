package com.spring.its_here.domain.store.service;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.store.dto.request.StoreCreateRequestDto;
import com.spring.its_here.domain.store.dto.request.StoreUpdateRequestDto;
import com.spring.its_here.domain.store.dto.response.StoreCreateResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreGetOneResponseDto;
import com.spring.its_here.domain.store.dto.response.StoreUpdateResponseDto;
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
        void success() {

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
        void duplicate_store_name() {

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
        void not_exists_category() {

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
        @DisplayName("숨김 처리된 카테고리")
        void hidden_category() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();

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

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 숨김 처리된 카테고리 생성
            Category hiddenCategory = Category.createCategory("치킨", true);
            ReflectionTestUtils.setField(hiddenCategory, "id", categoryId);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(hiddenCategory));

            // when & then
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.createStore(userDetails, requestDto)
                    );

            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_HIDDEN);

            verify(storeRepository, never())
                    .save(any(Store.class));
        }

        @Test
        @DisplayName("없거나 삭제된 지역")
        void not_exists_area() {

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
        void success() {

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
            assertThat(responseDto.categoryHasHidden()).isEqualTo(category.isHasHidden());
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
                            false,
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
                            true,
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
            assertThat(result.getContent().get(0).categoryHasHidden()).isEqualTo(dto1.categoryHasHidden());

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
                            Sort.by("name").descending()
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

    @Nested
    @DisplayName("가게 수정")
    class UpdateStore {

        @Test
        @DisplayName("성공")
        void success() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "manager1",
                    "Manager1!!",
                    "매니저",
                    UserRole.MANAGER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("중식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

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

            Category newCategory = Category.createCategory("양식", false);

            Area newArea = Area.create("서울특별시", "성동구", "성수2가제3동");

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId))
                    .thenReturn(false);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(newCategory));

            when(areaRepository.findByIdAndDeletedAtIsNull(areaId))
                    .thenReturn(Optional.of(newArea));

            // 서비스 호출
            StoreUpdateResponseDto responseDto =
                    storeService.updateStore(userDetails, storeId, requestDto);

            // 응답 Dto 검증
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(requestDto.name());
            assertThat(responseDto.address()).isEqualTo(requestDto.address());
            assertThat(responseDto.area()).isEqualTo(newArea.getTown());
            assertThat(responseDto.category()).isEqualTo(newCategory.getName());
            assertThat(responseDto.hasOpen()).isEqualTo(requestDto.hasOpen());
            assertThat(responseDto.openAt()).isEqualTo(requestDto.openAt());
            assertThat(responseDto.closedAt()).isEqualTo(requestDto.closedAt());

            // Repository 호출 검증
            verify(storeRepository).findByIdAndDeletedAtIsNull(storeId);
            verify(storeRepository).existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId);
            verify(categoryRepository).findByIdAndDeletedAtIsNull(categoryId);
            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);

        }

        @Test
        @DisplayName("카테고리와 지역은 유지하고 나머지 정보만 수정")
        void success_without_category_area(){

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "을지다락",
                            "서울 중구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(10, 0),
                            LocalTime.of(22, 0)
                    );

            UserEntity user = UserEntity.create(
                    "manager1",
                    "Manager1!!",
                    "매니저",
                    UserRole.MANAGER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("양식", false);

            Area area = Area.create("서울특별시", "성동구", "성수2동");

            Store store = Store.createStore(
                    "이전 가게",
                    "서울 강남구 압구정동",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(23, 0)
            );

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId))
                    .thenReturn(false);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            when(areaRepository.findByIdAndDeletedAtIsNull(areaId))
                    .thenReturn(Optional.of(area));

            // 서비스 호출
            StoreUpdateResponseDto responseDto =
                    storeService.updateStore(userDetails, storeId, requestDto);

            // 응답 Dto 검증
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(requestDto.name());
            assertThat(responseDto.address()).isEqualTo(requestDto.address());
            assertThat(responseDto.area()).isEqualTo(area.getTown());
            assertThat(responseDto.category()).isEqualTo(category.getName());
            assertThat(responseDto.hasOpen()).isEqualTo(requestDto.hasOpen());
            assertThat(responseDto.openAt()).isEqualTo(requestDto.openAt());
            assertThat(responseDto.closedAt()).isEqualTo(requestDto.closedAt());

            // Repository 호출 검증
            verify(storeRepository).findByIdAndDeletedAtIsNull(storeId);
            verify(storeRepository).existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId);
            verify(categoryRepository).findByIdAndDeletedAtIsNull(categoryId);
            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);

        }

        @Test
        @DisplayName("관리자가 수정하는 경우")
        void success_update_by_master() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            UserEntity user2 = UserEntity.create(
                    "master1",
                    "Master1!!",
                    "마스터",
                    UserRole.MASTER
            );

            ReflectionTestUtils.setField(user2, "id", 2L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user2);

            Category category = Category.createCategory("중식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

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

            Category newCategory = Category.createCategory("양식", false);

            Area newArea = Area.create("서울특별시", "성동구", "성수2가제3동");

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId))
                    .thenReturn(false);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(category));

            when(areaRepository.findByIdAndDeletedAtIsNull(areaId))
                    .thenReturn(Optional.of(area));

            // 서비스 호출
            StoreUpdateResponseDto responseDto =
                    storeService.updateStore(userDetails, storeId, requestDto);

            // 응답 Dto 검증
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.name()).isEqualTo(requestDto.name());
            assertThat(responseDto.address()).isEqualTo(requestDto.address());
            assertThat(responseDto.area()).isEqualTo(area.getTown());
            assertThat(responseDto.category()).isEqualTo(category.getName());
            assertThat(responseDto.hasOpen()).isEqualTo(requestDto.hasOpen());
            assertThat(responseDto.openAt()).isEqualTo(requestDto.openAt());
            assertThat(responseDto.closedAt()).isEqualTo(requestDto.closedAt());

            // Repository 호출 검증
            verify(storeRepository).findByIdAndDeletedAtIsNull(storeId);
            verify(storeRepository).existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId);
            verify(categoryRepository).findByIdAndDeletedAtIsNull(categoryId);
            verify(areaRepository).findByIdAndDeletedAtIsNull(areaId);

        }

        @Test
        @DisplayName("없거나 삭제된 가게 정보를 수정하려는 경우")
        void not_exists_or_deleted_store() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 가게가 삭제되거나 없다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.updateStore(userDetails, storeId, requestDto)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_FOUND);

            verify(storeRepository, never())
                    .existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId);

            verify(categoryRepository, never())
                    .findByIdAndDeletedAtIsNull(categoryId);

            verify(areaRepository, never())
                    .findByIdAndDeletedAtIsNull(areaId);

        }

        @Test
        @DisplayName("숨김 처리된 카테고리로 수정")
        void hidden_category() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails = new CustomUserDetails(user);

            Category category = Category.createCategory("중식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

            Store store = Store.createStore(
                    "기존가게",
                    "서울 성동구",
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

            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(
                    requestDto.name(), storeId))
                    .thenReturn(false);

            // 숨김 카테고리
            Category hiddenCategory =
                    Category.createCategory("한식", true);

            ReflectionTestUtils.setField(hiddenCategory, "id", categoryId);

            when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .thenReturn(Optional.of(hiddenCategory));

            // when
            ItsHereException exception = assertThrows(
                    ItsHereException.class,
                    () -> storeService.updateStore(userDetails, storeId, requestDto)
            );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.CATEGORY_HIDDEN);

            verify(areaRepository, never())
                    .findByIdAndDeletedAtIsNull(any());
        }

        @Test
        @DisplayName("중복되는 가게 이름으로 수정하려는 경우")
        void already_exists_store_name() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("패스트푸드", false);

            Area area = Area.create("서울특별시", "종로구", "창신1동");

            Store store = Store.createStore(
                    "롯데리아 종로5가점",
                    "서울 종로구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // 이름이 같은 다른 가게가 존재한다고 가정
            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId))
                    .thenReturn(true);

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.updateStore(userDetails, storeId, requestDto)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NAME_DUPLICATE);

            verify(categoryRepository, never())
                    .findByIdAndDeletedAtIsNull(categoryId);

            verify(areaRepository, never())
                    .findByIdAndDeletedAtIsNull(areaId);

        }

        @Test
        @DisplayName("자신의 가게가 아닌데 수정 시도하는 경우")
        void when_owner_not_match_store_owner() {

            // given
            UUID categoryId = UUID.randomUUID();
            UUID areaId = UUID.randomUUID();
            UUID storeId = UUID.randomUUID();

            StoreUpdateRequestDto requestDto =
                    new StoreUpdateRequestDto(
                            "보어앤헝그리",
                            "서울 성동구",
                            false,
                            areaId,
                            categoryId,
                            LocalTime.of(12, 0),
                            LocalTime.of(21, 0)
                    );

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            UserEntity user2 = UserEntity.create(
                    "another1",
                    "Another1!!",
                    "낫주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user2, "id", 2L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user2);

            Category category = Category.createCategory("중식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

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

            Category newCategory = Category.createCategory("양식", false);

            Area newArea = Area.create("서울특별시", "성동구", "성수2가제3동");

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            when(storeRepository.existsByNameAndDeletedAtIsNullAndIdNot(requestDto.name(), storeId))
                    .thenReturn(false);

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.updateStore(userDetails, storeId, requestDto)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_OWNED);

            verify(categoryRepository, never())
                    .findByIdAndDeletedAtIsNull(categoryId);

            verify(areaRepository, never())
                    .findByIdAndDeletedAtIsNull(areaId);

        }

    }

    @DisplayName("가게 삭제")
    @Nested
    class DeleteStore {

        @Test
        @DisplayName("성공")
        void success() {

            // given
            UUID storeId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "owner1",
                    "oWNER1!!",
                    "갓생",
                    UserRole.OWNER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            Category category = Category.createCategory("일식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

            Store store = Store.createStore(
                    "오레노라멘 강남점",
                    "서울 강남구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(11, 0),
                    LocalTime.of(22, 0)
            );

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // 서비스 호출
            storeService.deleteStore(userDetails, storeId);

            // 검증
            assertThat(store.getDeletedAt()).isNotNull();

            assertThat(store.getDeletedBy()).isEqualTo(userDetails.getUserId());

            // Repository 호출 검증
            verify(storeRepository).findByIdAndDeletedAtIsNull(storeId);

        }

        @Test
        @DisplayName("관리자가 가게 삭제")
        void success_delete_store_by_master() {

            // given
            UUID storeId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "owner1",
                    "oWNER1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(user, "id", 1L);

            UserEntity master = UserEntity.create(
                    "master1",
                    "MASTEr!!",
                    "마스터",
                    UserRole.MASTER
            );

            // Reflection으로 PK를 넣어줌
            ReflectionTestUtils.setField(master, "id", 2L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(master);

            Category category = Category.createCategory("일식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

            Store store = Store.createStore(
                    "오레노라멘 강남점",
                    "서울 강남구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(11, 0),
                    LocalTime.of(22, 0)
            );

            // 모든 검증을 통과한다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // 서비스 호출
            storeService.deleteStore(userDetails, storeId);

            // 검증
            assertThat(store.getDeletedAt()).isNotNull();

            assertThat(store.getDeletedBy()).isEqualTo(userDetails.getUserId());

            // Repository 호출 검증
            verify(storeRepository).findByIdAndDeletedAtIsNull(storeId);

        }

        @Test
        @DisplayName("없거나 삭제된 가게에 대해 삭제 시도")
        void not_exists_or_deleted_store() {

            // given
            UUID storeId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user);

            // 가게가 삭제되거나 없다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.empty());

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.deleteStore(userDetails, storeId)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_FOUND);

        }

        @Test
        @DisplayName("가게 주인이 다른 사람의 가게 삭제 시도")
        void owner_try_to_delete_others_store() {

            // given
            UUID storeId = UUID.randomUUID();

            UserEntity user = UserEntity.create(
                    "owner1",
                    "Owner1!!",
                    "찐주인",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user, "id", 1L);

            UserEntity user2 = UserEntity.create(
                    "notOwner",
                    "NotOwner1!!",
                    "주인아님",
                    UserRole.OWNER
            );

            ReflectionTestUtils.setField(user2, "id", 2L);

            CustomUserDetails userDetails =
                    new CustomUserDetails(user2);

            Category category = Category.createCategory("중식", false);

            Area area = Area.create("서울특별시", "강남구", "삼성동");

            Store store = Store.createStore(
                    "희래궁 역삼점",
                    "서울 강남구",
                    user,
                    category,
                    area,
                    true,
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0)
            );

            // 가게가 삭제되거나 없다고 가정
            when(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .thenReturn(Optional.of(store));

            // when
            ItsHereException exception =
                    assertThrows(
                            ItsHereException.class,
                            () -> storeService.deleteStore(userDetails, storeId)
                    );

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(ErrorCode.STORE_NOT_OWNED);

        }

        }
    }

}