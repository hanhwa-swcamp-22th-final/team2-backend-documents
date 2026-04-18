package com.team2.documents.command.application.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthUserResponse;

/**
 * PI/PO 수정·삭제 오너십 검증.
 *
 * 허용 조건:
 * - ADMIN → 통과 (Security Context 권한 검사)
 * - 작성자 본인 (requesterId == managerId) → 통과
 * - 팀장 (positionLevel == 1) 이며 문서 작성자와 같은 팀 → 통과
 *
 * Controller 의 @PreAuthorize 가 ADMIN/SALES 로 1차 필터하지만,
 * SALES 가 남의 문서를 건드리지 못하게 서비스 레이어에서 한 번 더 막는다.
 */
@Service
public class DocumentOwnershipGuard {

    private final AuthFeignClient authFeignClient;

    public DocumentOwnershipGuard(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    public void assertCanMutate(Long requesterId, Long managerId) {
        if (isAdmin()) return;
        if (requesterId == null) {
            throw new AccessDeniedException("사용자 식별 불가");
        }
        if (managerId != null && requesterId.equals(managerId)) return;

        AuthUserResponse requester;
        try {
            requester = authFeignClient.getUser(requesterId);
        } catch (Exception e) {
            throw new AccessDeniedException("사용자 정보 조회 실패");
        }
        if (requester == null) throw new AccessDeniedException("사용자 없음");

        Integer posLevel = requester.getPosition() != null ? requester.getPosition().getPositionLevel() : null;
        if (posLevel != null && posLevel == 1) {
            // 팀장이라도 다른 팀 문서는 불가
            if (managerId == null) {
                throw new AccessDeniedException("문서 작성자 정보 없음");
            }
            AuthUserResponse manager;
            try {
                manager = authFeignClient.getUser(managerId);
            } catch (Exception e) {
                throw new AccessDeniedException("문서 작성자 정보 조회 실패");
            }
            if (manager != null && requester.getTeamId() != null
                    && requester.getTeamId().equals(manager.getTeamId())) {
                return;
            }
        }
        throw new AccessDeniedException("본인 또는 팀장·ADMIN 만 수정/삭제할 수 있습니다.");
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
