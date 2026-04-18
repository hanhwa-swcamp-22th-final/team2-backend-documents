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

    // 내부 전용 경로로 호출 — InternalTokenFeignInterceptor 가 X-Internal-Token 자동 주입.
    // 일반 /api/users/{id} 는 hasRole("ADMIN") 이라 팀장/영업 세션에선 401 나므로 internal 경로로 고정.
    @GetMapping("/api/users/internal/{userId}")
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

    /**
     * 결재자 후보 조회 — 해당 팀의 팀장(position_level=1) + 전체 ADMIN.
     * teamId null 이면 전 팀의 팀장.
     */
    @GetMapping("/api/users/internal/approvers")
    List<AuthInternalUserResponse> getApprovers(@RequestParam(name = "teamId", required = false) Integer teamId);

    /**
     * 팀 소속 active 사용자 ID 목록. PI/PO 팀 스코프 필터에 사용.
     * 빈 팀이면 빈 리스트. Fallback 시에도 빈 리스트 반환하여 호출부에서 "스코프 0" 로 안전히 처리.
     */
    @GetMapping("/api/users/internal/team/{teamId}/ids")
    List<Long> getTeamMemberIds(@PathVariable("teamId") Integer teamId);
}
