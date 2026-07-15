package com.spring.its_here.domain.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.area.repository.AreaRepository;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.category.repository.CategoryRepository;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.security.CustomUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@EnableJpaAuditing
@DataJpaTest
@Import({
        StoreRepositoryImpl.class,
        StoreRepositoryTest.QueryDslTestConfig.class
})
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private UserRepository userRepository;

    private Pageable pageable;

    private UserEntity user;

    private CustomUserDetails userDetails;

    @TestConfiguration
    static class QueryDslTestConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(
                0,
                10,
                Sort.by("createdAt").ascending()
        );

        user = UserEntity.create(
                "test1",
                "password",
                "닉네임",
                UserRole.OWNER
        );

        userRepository.save(user);

        userDetails =
                new CustomUserDetails(user);
    }


    @Test
    @DisplayName("가게 목록 조회 성공")
    void get_all_store_success() {

        // given
        Category category = createCategory("양식" , false);

        Area area = createArea("서울특별시", "강남구", "역삼동");

        Store store = createStore(
                category,
                area
        );

        // when
        Page<StoreGetAllResponseDto> result =
                storeRepository.getAllStores(
                        null,
                        null,
                        null,
                        pageable
                );

        // then
        assertThat(result.getTotalElements())
                .isEqualTo(1);

        StoreGetAllResponseDto responseDto =
                result.getContent().get(0);

        assertThat(responseDto.name())
                .isEqualTo(store.getName());

        assertThat(responseDto.category())
                .isEqualTo(category.getName());

        assertThat(responseDto.area())
                .isEqualTo(area.getTown());
    }

    @Test
    @DisplayName("지역명으로 가게 목록 조회 성공")
    void get_all_stores_by_area() {

        // given
        Category category = createCategory("양식" , false);

        Area area = createArea("서울특별시", "강남구", "역삼동");

        Store store = createStore(category, area);

        // when
        Page<StoreGetAllResponseDto> result =
                storeRepository.getAllStores(
                        null,
                        null,
                        "역삼동",
                        pageable
                );

        // then
        assertThat(result.getTotalElements())
                .isEqualTo(1);

        StoreGetAllResponseDto responseDto =
                result.getContent().get(0);

        assertThat(responseDto.name())
                .isEqualTo(store.getName());

        assertThat(responseDto.category())
                .isEqualTo(category.getName());

        assertThat(responseDto.area())
                .isEqualTo(area.getTown());
    }

    @Test
    @DisplayName("삭제된 가게는 조회되지 않음")
    void deleted_store_is_not_selected() {

        // given
        Category category = createCategory("양식" , false);

        Area area = createArea("서울특별시", "강남구", "역삼동");

        Store store = createStore(
                category,
                area
        );

        store.delete(userDetails.getUserId()); // 가게 삭제

        // when
        Page<StoreGetAllResponseDto> result =
                storeRepository.getAllStores(
                        null,
                        null,
                        null,
                        pageable
                );

        // then
        assertThat(result.getTotalElements())
                .isEqualTo(0);

    }

    @Test
    @DisplayName("삭제된 카테고리로 필터링하면 조회되지 않음")
    void deleted_category_filtering() {

        // given
        Category category = createCategory("양식", false);

        category.delete(userDetails.getUserId()); // 카테고리 삭제

        categoryRepository.save(category);

        createStore(
                category,
                createArea("서울특별시", "강남구", "역삼동")
        );

        // when
        Page<StoreGetAllResponseDto> result =
                storeRepository.getAllStores(
                        null,
                        "양식",
                        "양재동",
                        pageable
                );

        // then
        assertThat(result.getTotalElements())
                .isZero();
    }

    private Category createCategory(String name, Boolean hasHidden) {
        Category category = Category.createCategory(name, hasHidden);
        return categoryRepository.save(category);
    }

    private Area createArea(String city, String district, String town) {
        Area area = Area.create(city, district, town);
        return areaRepository.save(area);
    }

    private Store createStore(
            Category category,
            Area area
    ) {

        Store store = Store.createStore("을지다락 강남점", "서울특별시 강남구 강남대로96길 22",
                userDetails.getUser(), category, area,
                true, LocalTime.of(9, 0), LocalTime.of(22, 0));

        return storeRepository.save(store);
    }

    private Store createStore1(
            Category category,
            Area area
    ) {

        Store store = Store.createStore("행복식당", "서울특별시 강남구 강남대로96길 22",
                userDetails.getUser(), category, area,
                true, LocalTime.of(9, 0), LocalTime.of(22, 0));

        return storeRepository.save(store);
    }

}