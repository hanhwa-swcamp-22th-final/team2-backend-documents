package com.team2.documents.command.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeignClient> {

    private static final Logger log = LoggerFactory.getLogger(AuthFeignFallbackFactory.class);

    @Override
    public AuthFeignClient create(Throwable cause) {
        return userId -> {
            log.warn("[fallback] auth-service getUser({}) unavailable: {}",
                    userId, cause != null ? cause.getMessage() : "unknown");
            // 호출부에서 null 체크하므로 null 반환은 피하고 placeholder 객체 반환
            AuthUserResponse fallback = new AuthUserResponse();
            fallback.setUserId(userId);
            fallback.setName("(unknown user)");
            return fallback;
        };
    }
}
