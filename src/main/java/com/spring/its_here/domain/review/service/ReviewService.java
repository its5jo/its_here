package com.spring.its_here.domain.review.service;


import com.spring.its_here.domain.order.entity.Order;
import com.spring.its_here.domain.order.repository.OrderRepository;
import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
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

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Transactional
    public ReviewCreateResponseDto create(ReviewCreateRequestDto reviewCreateRequestDto) {
        Long userId = authenticationFacade.getCurrentUserId();

        Order order = orderRepository.findById(reviewCreateRequestDto.orderId())
                .orElseThrow(() -> new ItsHereException(ErrorCode.ORDER_NOT_FOUND));

        if(!order.getUserId().equals(userId)){
            throw new ItsHereException(ErrorCode.REVIEW_FORBIDDEN);
        }

        Review reviewCreate = Review.create(
                reviewCreateRequestDto.rating(),
                reviewCreateRequestDto.content(),
                order
//                order.StoreId(),
        );

        Review reviewSave = reviewRepository.save(reviewCreate);

        return new ReviewCreateResponseDto(
                reviewSave.getId(),
                reviewSave.getOrder().getId(),
                reviewSave.getStoreId(),
                reviewSave.getUserId()
        );
    }
}
