package com.team2.documents.command.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthInternalUserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 지시서 발행 시 담당자 후보(생산/출하 role 활성 유저) 를 SALES 도 조회할 수 있게
 * 노출하는 프록시 엔드포인트. Auth 서비스의 /api/users/internal/by-role 은 X-Internal-Token
 * 보호 경로라 프론트에서 직접 호출 불가 → Documents 가 Feign 으로 조회 후 재노출.
 *
 * MSA 경계 유지: Documents 는 Auth teams/departments/users 에 직접 JOIN 하지 않고
 * Feign 으로만 접근한다 (CLAUDE.md 규칙).
 */
@Tag(name = "담당자 후보", description = "지시서 발행 모달에서 담당자 드롭다운을 채우기 위한 조회 API")
@RestController
@RequestMapping("/api")
public class AssignableUserController {

    private final AuthFeignClient authFeignClient;

    public AssignableUserController(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES','PRODUCTION','SHIPPING')")
    @Operation(summary = "담당자 후보 조회", description = "role 별 활성 사용자 목록. 예: role=production 은 생산담당자 후보.")
    @GetMapping("/assignable-users")
    public ResponseEntity<List<AuthInternalUserResponse>> getAssignableUsers(
            @RequestParam("role") String role) {
        // Feign 인터셉터가 X-Internal-Token 을 자동 주입한다 (InternalTokenFeignInterceptor).
        List<AuthInternalUserResponse> users = authFeignClient.getUsersByRole(role, "active");
        return ResponseEntity.ok(users == null ? List.of() : users);
    }
}
