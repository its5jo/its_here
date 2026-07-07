package com.spring.its_here.domain.user.controller;

import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.service.UserService;
import com.spring.its_here.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "회원가입 성공",
                        "SUCCESS",
                        userService.signup(userCreateRequestDto)
                ));
    }

}
