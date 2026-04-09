package com.team2.documents.command.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "auth-service",
        url = "${auth.service.url:http://localhost:8011}",
        fallbackFactory = AuthFeignFallbackFactory.class
)
public interface AuthFeignClient {

    @GetMapping("/api/users/{userId}")
    AuthUserResponse getUser(@PathVariable("userId") Long userId);

    /**
     * 내부 전용: 특정 role + userStatus 사용자 목록 조회.
     * Auth 서비스 InternalApiTokenFilter 가 X-Internal-Token 헤더를 검증한다.
     * (InternalTokenFeignInterceptor 가 /internal 경로 감지 후 자동 주입)
     */
    @GetMapping("/api/users/internal/by-role")
    List<AuthInternalUserResponse> getUsersByRole(
            @RequestParam("role") String role,
            @RequestParam(name = "userStatus", required = false) String userStatus);
}
