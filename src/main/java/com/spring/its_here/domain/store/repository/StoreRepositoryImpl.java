package com.spring.its_here.domain.store.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.its_here.domain.area.entity.QArea;
import com.spring.its_here.domain.category.entity.QCategory;
import com.spring.its_here.domain.store.dto.response.StoreGetAllResponseDto;
import com.spring.its_here.domain.store.entity.QStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Page<StoreGetAllResponseDto> getAllStores(String name, String category, Pageable pageable) {

        QStore qStore = QStore.store;
        QCategory qCategory = QCategory.category;
        QArea qArea = QArea.area;

        // 가게의 카테고리가 삭제된 경우 카테고리 이름 뒤에 (삭제됨) 현출
        StringExpression categoryName = categoryNameExpression(qCategory);

        // 가게의 지역이 삭제된 경우 지역 이름 뒤에 (삭제됨) 현출
        StringExpression townName = townNameExpression(qArea);

        // 평균 평점 계산
        NumberExpression<Double> calculateAverageRating = new CaseBuilder()
                .when(qStore.reviewTotalCount.eq(0L))
                .then(0.0)
                .otherwise(
                        qStore.reviewTotalRating.divide(
                                qStore.reviewTotalCount.doubleValue()
                        )
                );

        // 카테고리 및 가게 이름으로 페이지에 맞는 가게 목록 조회
        List<StoreGetAllResponseDto> result = queryFactory
                .select(Projections.constructor(
                        StoreGetAllResponseDto.class,
                        qStore.id,
                        qStore.name,
                        categoryName,
                        qStore.address,
                        townName,
                        calculateAverageRating,
                        qStore.hasOpen
                ))
                .from(qStore)
                .leftJoin(qStore.category, qCategory)
                .leftJoin(qStore.area, qArea)
                .where(
                        storeNameContains(name),
                        categoryNameEqual(category),
                        qStore.deletedAt.isNull()
                )
                .offset(pageable.getOffset())
                .orderBy(qStore.createdAt.asc())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = Optional.ofNullable(
                queryFactory
                .select(qStore.count())
                .from(qStore)
                .leftJoin(qStore.category, qCategory)
                .where(
                        storeNameContains(name),
                        categoryNameEqual(category),
                        qStore.deletedAt.isNull()
                )
                .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(result, pageable, total);
    }

    private StringExpression categoryNameExpression(QCategory qCategory) {
        return new CaseBuilder()
                .when(qCategory.deletedAt.isNotNull())
                .then(qCategory.name.concat("(삭제됨)"))
                .otherwise(qCategory.name);
    }

    private StringExpression townNameExpression(QArea qArea) {
        return new CaseBuilder()
                .when(qArea.deletedAt.isNotNull())
                .then(qArea.town.concat("(삭제됨)"))
                .otherwise(qArea.town);
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

        // 입력된 카테고리가 삭제된 경우 조회 결과 없음
        return QCategory.category.name.eq(category)
                .and(QCategory.category.deletedAt.isNull());
    }

}