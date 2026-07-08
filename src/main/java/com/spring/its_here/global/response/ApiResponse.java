package com.spring.its_here.global.response;


public record ApiResponse<T>(
        String message,
        String code,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, "SUCCESS", data);
    }
}
