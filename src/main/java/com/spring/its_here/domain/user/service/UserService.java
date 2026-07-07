package com.spring.its_here.domain.user.service;

import com.spring.its_here.domain.user.dto.request.UserCreateRequestDto;
import com.spring.its_here.domain.user.dto.response.UserResponseDto;
import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.repository.UserRepository;
import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto signup(UserCreateRequestDto userCreateRequestDto) {
        // 동일한 닉네임 존재 확인
        if (userRepository.existsByUsernameAndHasDeletedFalse(userCreateRequestDto.username())) {
            throw new ItsHereException(ErrorCode.DUPLICATE_USERNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateRequestDto.password());

        // 사용자 생성
        UserEntity user = UserEntity.create(
                userCreateRequestDto.username(),
                encodedPassword,
                userCreateRequestDto.nickname(),
                userCreateRequestDto.role()
        );

        // 사용자 저장
        userRepository.save(user);

        // 생성자 저장
        user.updateCreatedBy(user.getId());

        return new UserResponseDto(user.getId());
    }
}
