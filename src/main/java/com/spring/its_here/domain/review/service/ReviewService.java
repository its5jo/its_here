package com.spring.its_here.domain.review.service;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.domain.review.dto.response.ReviewCreateResponseDto;
import com.spring.its_here.domain.review.entity.Review;
import com.spring.its_here.domain.review.repository.ReviewRepository;
import com.spring.its_here.global.advice.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

//    private final OrderRepository orderRepository;

    @Transactional
    public ReviewCreateResponseDto create(ReviewCreateRequestDto reviewCreateRequestDto) {

//        Order order = orderRepository.findById().orElseThrow(()-> ErrorCode.AUTH_UNAUTHORIZED );

        Review reviewCreate = Review.create(
                reviewCreateRequestDto.rating(),
                reviewCreateRequestDto.content()
//                order.getId(),
//                order.StoreId(),
//                order.getUserId()
        );

        Review reviewSave = reviewRepository.save(reviewCreate);

        return new ReviewCreateResponseDto(
                reviewSave.getId(),
                reviewSave.getOrderId(),
                reviewSave.getStoreId(),
                reviewSave.getUserId()
        );
    }
}
