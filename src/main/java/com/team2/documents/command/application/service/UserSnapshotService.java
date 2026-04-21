package com.team2.documents.command.application.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;

@Service
public class UserSnapshotService {

    private final AuthFeignClient authFeignClient;

    public UserSnapshotService(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    // /api/approval-requests size=1000 호출 시 페이지당 최대 2000건 (requester+approver) Feign
    // 호출이 발생해 8s timeout 초과. 사용자 이름은 거의 안 바뀌므로 캐싱.
    // unless: Feign 실패 fallback (userId.toString()) 은 캐시 미저장 → 다음 시도에 재조회.
    @Cacheable(value = "userDisplayNames",
            unless = "#result == null || #result.equals(T(java.lang.String).valueOf(#userId))")
    public String resolveRequesterDisplayName(Long userId) {
        if (userId == null) {
            return "";
        }
        try {
            AuthUserResponse response = authFeignClient.getUser(userId);
            if (response != null && response.getName() != null && !response.getName().isBlank()) {
                return response.getName();
            }
        } catch (RuntimeException ignored) {
            return String.valueOf(userId);
        }
        return String.valueOf(userId);
    }
}
