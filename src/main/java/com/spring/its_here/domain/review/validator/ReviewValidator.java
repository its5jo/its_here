package com.spring.its_here.domain.review.validator;


import com.spring.its_here.domain.review.dto.request.ReviewCreateRequestDto;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;

public class ReviewValidator {
    public static void validateCreate(ReviewCreateRequestDto reviewCreateRequestDto) {
        validateRating(reviewCreateRequestDto.rating());
        validateContent(reviewCreateRequestDto.content());
    }

    public static void validateRating(double rating) {
        if (!Double.isFinite(rating) || rating < 1.0 || rating > 5.0) {
            throw new ItsHereException(ErrorCode.INVALID_RATING);
        }
    }

    public static void validateContent(String content) {
        if (content != null && content.length() > 255) {
            throw new ItsHereException(ErrorCode.CONTENT_TOO_LONG);
        }
    }
}
