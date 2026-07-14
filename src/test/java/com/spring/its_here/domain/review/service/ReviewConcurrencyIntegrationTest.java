package com.spring.its_here.domain.review.service;

import com.spring.its_here.domain.area.entity.Area;
import com.spring.its_here.domain.category.entity.Category;
import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class ReviewConcurrencyIntegrationTest {
    @Autowired
    EntityManagerFactory entityManagerFactory;

    UUID reviewId;

    // 트러블 기록: Unique index or primary key violation, P_USER(USERNAME) VALUES ('username') 등 + 길이 제한 에러
    String random = UUID.randomUUID().toString().substring(0, 5);

    @BeforeEach()
    void setUp() {
        reviewId = saveTestReview();
    }

    // 두 요청이 같은 리뷰의 같은 버전을 읽고 첫번째 요청이 먼저 수행 후 두번째 요청이 예전 버전으로 수정하다가 실패하는 상황
    @Test
    @DisplayName("먼저 수정된 리뷰를 이전 버전으로 다시 수정 시 예외")
    void updateSameReview_onlyOne_success() {
        EntityManager firstEntityManager = entityManagerFactory.createEntityManager();
        EntityManager secondEntityManager = entityManagerFactory.createEntityManager();

        // 두 요청이 같은 리뷰 조회
        Review firstReview = firstEntityManager.find(
                Review.class,
                reviewId
        );
        Review secondReview = secondEntityManager.find(
                Review.class,
                reviewId
        );

        // 첫번째 요청 성공
        EntityTransaction firstTransaction = firstEntityManager.getTransaction();

        firstTransaction.begin();
        firstReview.updateReview(4.0, "첫 번째 수정");
        firstTransaction.commit();

        // 두 번째 요청은 예전 version을 가지고 있어서 실패
        EntityTransaction secondTransaction = secondEntityManager.getTransaction();

        secondTransaction.begin();
        secondReview.updateReview(5.0, "두 번째 수정");

        assertThatThrownBy(secondTransaction::commit)
                .isInstanceOf(RuntimeException.class);

        firstEntityManager.close();
        secondEntityManager.close();
    }

    // 두 요청이 같은 리뷰의 같은 버전을 읽고 첫 번째 요청이 먼저 수정한 후 두 번째 요청이 이전 버전으로 삭제를 시도하다가 실패하는 상황
    @Test
    @DisplayName("같은 리뷰의 수정과 삭제가 동시에 요청되면 하나의 요청만 성공")
    void updateAndDeleteSameReview_onlyOne_success() {
        EntityManager updateEntityManager = entityManagerFactory.createEntityManager();
        EntityManager deleteEntityManager = entityManagerFactory.createEntityManager();

        // 두 요청이 같은 리뷰 조회
        Review updateaReview = updateEntityManager.find(
                Review.class,
                reviewId
        );
        Review deleteReview = deleteEntityManager.find(
                Review.class,
                reviewId
        );

        EntityTransaction updateTransaction = updateEntityManager.getTransaction();
        EntityTransaction deleteTransaction = deleteEntityManager.getTransaction();

        // 수정 요청 먼저 성공
        updateTransaction.begin();

        updateaReview.updateReview(
                5.0,
                "수정된 내용"
        );
        updateTransaction.commit();

        // 삭제된 요청은 예전 version을 가지고 있어서 실패
        deleteTransaction.begin();
        deleteReview.delete(1L);

        assertThatThrownBy(deleteTransaction::commit)
                .isInstanceOf(RuntimeException.class);

        updateEntityManager.close();
        deleteEntityManager.close();
    }

    // 트러블 기록: Not-null property references a transient value, Review.store -> Store. 즉 Review가 참조하는 객체(user, store, order)를 먼저 영속화해야 함 (persist / flush)
    private UUID saveTestReview() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction entityTransaction = entityManager.getTransaction();

        entityTransaction.begin();

        UserEntity user = saveTestUser(entityManager);
        Area area = saveTestArea(entityManager);
        Category category = saveTestCategory(entityManager);

        Store store = saveTestStore(
                entityManager,
                user,
                category,
                area
        );

        Order order = saveTestOrder(
                entityManager,
                store,
                user
        );

        Review review = Review.savedReview(
                3.0,
                "기존 리뷰",
                user,
                store,
                order
        );

        entityManager.persist(review);
        entityManager.flush();

        UUID savedReviewId = review.getId();

        entityTransaction.commit();
        entityManager.close();

        return savedReviewId;
    }

    private UserEntity saveTestUser(EntityManager entityManager) {

        UserEntity user = UserEntity.create(
                "username-" + random,
                "password",
                "nickname-" + random,
                UserRole.CUSTOMER
        );
        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    private Area saveTestArea(EntityManager entityManager) {
        Area area = Area.create(
                "city-" + random,
                "district",
                "town"
        );
        entityManager.persist(area);
        entityManager.flush();

        return area;
    }

    private Category saveTestCategory(EntityManager entityManager) {
        Category category = Category.createCategory(
                "name-" + random,
                false
        );
        entityManager.persist(category);
        entityManager.flush();

        return category;
    }

    private Store saveTestStore(
            EntityManager entityManager,
            UserEntity user,
            Category category,
            Area area
    ) {
        Store store = Store.createStore(
                "name-" + random,
                "address",
                user,
                category,
                area,
                true,
                null,
                null
        );
        entityManager.persist(store);
        entityManager.flush();

        return store;
    }

    private Order saveTestOrder(
            EntityManager entityManager,
            Store store,
            UserEntity user
    ) {
        Order order = Order.create(
                store.getId(),
                user.getId(),
                "address",
                "memo",
                20000
        );
        entityManager.persist(order);
        entityManager.flush();

        return order;
    }
}
