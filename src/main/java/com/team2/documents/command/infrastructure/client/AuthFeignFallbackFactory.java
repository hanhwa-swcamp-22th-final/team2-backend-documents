package com.team2.documents.command.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeignClient> {

    private static final Logger log = LoggerFactory.getLogger(AuthFeignFallbackFactory.class);

    @Override
    public AuthFeignClient create(Throwable cause) {
        return new AuthFeignClient() {
            @Override
            public AuthUserResponse getUser(Long userId) {
                log.warn("[fallback] auth-service getUser({}) unavailable: {}",
                        userId, cause != null ? cause.getMessage() : "unknown");
                AuthUserResponse fallback = new AuthUserResponse();
                fallback.setUserId(userId);
                fallback.setName("(unknown user)");
                return fallback;
            }

            @Override
            public List<AuthInternalUserResponse> getUsersByRole(String role, String userStatus) {
                log.warn("[fallback] auth-service getUsersByRole(role={}, status={}) unavailable: {}",
                        role, userStatus, cause != null ? cause.getMessage() : "unknown");
                return Collections.emptyList();
            }

            @Override
            public List<AuthInternalUserResponse> getApprovers(Integer teamId) {
                log.warn("[fallback] auth-service getApprovers(teamId={}) unavailable: {}",
                        teamId, cause != null ? cause.getMessage() : "unknown");
                return Collections.emptyList();
            }

            @Override
            public List<Long> getTeamMemberIds(Integer teamId) {
                log.warn("[fallback] auth-service getTeamMemberIds(teamId={}) unavailable: {}",
                        teamId, cause != null ? cause.getMessage() : "unknown");
                // 빈 리스트 → 팀 스코프 0 → "해당 팀원 없음" 으로 안전히 빈 페이지 반환하도록 함
                return Collections.emptyList();
            }
        };
    }
}
