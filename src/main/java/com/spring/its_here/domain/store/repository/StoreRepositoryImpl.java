package com.spring.its_here.domain.store.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.its_here.domain.area.entity.QArea;
import com.spring.its_here.domain.category.entity.QCategory;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.entity.QStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    // QueryDSL 쿼리를 작성하기 위한 객체
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StoreGetAllResponseDto> getAllStores(
            String name, String category, String town, Pageable pageable) {

        QStore qStore = QStore.store;
        QCategory qCategory = QCategory.category;
        QArea qArea = QArea.area;

        // 평균 평점 계산
        NumberExpression<Double> calculateAverageRating = new CaseBuilder()
                .when(qStore.reviewTotalCount.eq(0L))
                .then(0.0)
                .otherwise(
                        qStore.reviewTotalRating.divide(
                                qStore.reviewTotalCount.doubleValue()
                        )
                );

        List<StoreGetAllResponseDto> result = queryFactory
                .select(Projections.constructor(
                        StoreGetAllResponseDto.class,
                        qStore.id,
                        qStore.name,
                        qCategory.name,
                        qCategory.hasHidden,
                        qStore.address,
                        qArea.town,
                        calculateAverageRating,
                        qStore.hasOpen
                ))
                .from(qStore)
                .leftJoin(qStore.category, qCategory)
                .leftJoin(qStore.area, qArea)
                .where(
                        storeNameContains(name),
                        categoryNameEqual(category),
                        townNameEqual(town),
                        qStore.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .orderBy(getOrderBy(pageable))
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = Optional.ofNullable(
                queryFactory
                .select(qStore.count())
                .from(qStore)
                .leftJoin(qStore.category, qCategory)
                .leftJoin(qStore.area, qArea)
                .where(
                        storeNameContains(name),
                        categoryNameEqual(category),
                        townNameEqual(town),
                        qStore.deletedAt.isNull()
                )
                .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(result, pageable, total);
    }

    private BooleanExpression storeNameContains(String name) {
        return StringUtils.hasText(name)
                ? QStore.store.name.contains(name)
                : null;
    }

    private BooleanExpression categoryNameEqual(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }

        // 입력된 카테고리가 삭제되거나 숨겨진 경우 조회 결과 없음
        return QCategory.category.name.eq(category)
                .and(QCategory.category.hasHidden.isFalse())
                .and(QCategory.category.deletedAt.isNull());
    }

    private BooleanExpression townNameEqual(String town) {
        if (!StringUtils.hasText(town)) {
            return null;
        }

        // 입력된 지역이 삭제되거나 사용 불가능한 경우 조회 결과 없음
        return QArea.area.town.eq(town)
                .and(QArea.area.hasAvailable.isTrue())
                .and(QArea.area.deletedAt.isNull());
    }

    private OrderSpecifier<?> getOrderBy(Pageable pageable) {

        Sort.Order sort = pageable.getSort()
                .stream()
                .findFirst()
                .orElse(Sort.Order.desc("createdAt"));

        if (sort.getProperty().equals("name")) {
            return sort.isAscending() ? QStore.store.name.asc() : QStore.store.name.desc();
        }

        return sort.isAscending() ? QStore.store.createdAt.asc() : QStore.store.createdAt.desc();
    }

}