package com.spring.its_here.global.security;

import com.spring.its_here.domain.user.entity.UserEntity;
import com.spring.its_here.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsernameAndHasDeletedFalse(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long userId) {
        UserEntity user = userRepository.findByIdAndHasDeletedFalse(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return new CustomUserDetails(user);
    }
}
