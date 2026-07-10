package com.spring.its_here.domain.review.service;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.enums.OrderStatus;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public ReviewCreateResponseDto create(ReviewCreateRequestDto reviewCreateRequestDto) {
        Long userId = authenticationFacade.getCurrentUserId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ItsHereException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(reviewCreateRequestDto.orderId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(user.getId())) {
            throw new ItsHereException(ErrorCode.REVIEW_FORBIDDEN);
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ItsHereException(ErrorCode.REVIEW_ORDER_NOT_COMPLETED);
        }

        if (reviewRepository.existsByOrder_Id(order.getId())) {
            throw new ItsHereException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review reviewCreate = Review.create(
                reviewCreateRequestDto.rating(),
                reviewCreateRequestDto.content(),
                order,
                user
        );

        Review reviewSave = reviewRepository.save(reviewCreate);

        return new ReviewCreateResponseDto(
                reviewSave.getId(),
                reviewSave.getOrder().getId(),
                reviewSave.getOrder().getStoreId(),
                reviewSave.getOrder().getUserId()
        );
    }
}
