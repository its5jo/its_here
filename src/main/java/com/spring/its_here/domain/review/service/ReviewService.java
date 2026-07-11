package com.spring.its_here.domain.review.service;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import com.spring.its_here.domain.store.entity.Store;
import com.spring.its_here.domain.store.repository.StoreRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public ReviewCreateResponseDto createReview(
            ReviewCreateRequestDto reviewCreateRequestDto,
            CustomUserDetails userDetails
    ) {
        UserEntity user = userDetails.getUserEntity();

        Order order = orderRepository.findById(reviewCreateRequestDto.orderId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(user.getId())) {
            throw new ItsHereException(ErrorCode.REVIEW_FORBIDDEN);
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ItsHereException(ErrorCode.REVIEW_ORDER_NOT_COMPLETED);
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new ItsHereException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Store store = storeRepository.findById(order.getStoreId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.STORE_NOT_FOUND));

        Review reviewCreate = Review.savedReview(
                reviewCreateRequestDto.rating(),
                reviewCreateRequestDto.content(),
                user,
                store,
                order
        );

        Review reviewSave = reviewRepository.save(reviewCreate);
        store.accumulateReview(reviewCreateRequestDto.rating());

        return new ReviewCreateResponseDto(
                reviewSave.getId(),
                reviewSave.getOrder().getId(),
                reviewSave.getStore().getId(),
                reviewSave.getUser().getId()
        );
    }
}
