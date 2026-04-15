package com.team2.documents.command.infrastructure.client;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * STAFF 사용자가 PO 수정/삭제 결재를 요청할 때, 해당 사용자 팀의 팀장을 결재자로 결정한다.
 * 팀장 부재 시 ADMIN 을 fallback 으로 선택한다.
 *
 * 기존 로직은 결재자를 1L(admin) 로 하드코딩하여 요청자 팀과 무관하게 admin 에게만
 * 결재 요청이 쌓이고, 팀 단위 결재 drop-down 필터와 불일치가 발생했다.
 */
@Component
public class ApproverResolver {

    private final AuthFeignClient authFeignClient;

    public ApproverResolver(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    public Long resolveApproverId(Long requesterUserId) {
        AuthUserResponse requester = authFeignClient.getUser(requesterUserId);
        Integer teamId = requester != null ? requester.getTeamId() : null;

        List<AuthInternalUserResponse> approvers = authFeignClient.getApprovers(teamId);
        if (approvers == null || approvers.isEmpty()) {
            throw new IllegalStateException("결재자를 찾을 수 없습니다. userId=" + requesterUserId);
        }

        // 팀장(ADMIN 이 아닌 사용자) 우선, 없으면 ADMIN 중 첫 번째.
        return approvers.stream()
                .filter(a -> !"ADMIN".equalsIgnoreCase(a.getUserRole()))
                .findFirst()
                .or(() -> approvers.stream().findFirst())
                .map(a -> a.getUserId().longValue())
                .orElseThrow(() -> new IllegalStateException("결재자 매핑 실패. userId=" + requesterUserId));
    }
}
