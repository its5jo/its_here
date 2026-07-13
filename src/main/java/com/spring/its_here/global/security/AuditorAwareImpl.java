package com.spring.its_here.global.security;

import com.spring.its_here.global.advice.ErrorCode;
import com.spring.its_here.global.advice.ItsHereException;
import com.spring.its_here.global.constant.SystemConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final AuthenticationFacade authenticationFacade;

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            return Optional.of(authenticationFacade.getCurrentUserId());
        } catch (ItsHereException e) {
            if (e.getErrorCode() == ErrorCode.AUTH_UNAUTHORIZED) {
                return Optional.of(SystemConstant.SYSTEM_USER_ID);
            }

            throw e;
        }
    }
}
