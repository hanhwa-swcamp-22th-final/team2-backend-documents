package com.team2.documents.command.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Auth 서비스 내부 엔드포인트 {@code /api/users/internal/by-role} 의 응답 DTO.
 * Auth 의 UserListResponse 와 필드 매칭. HATEOAS wrapper 없는 plain 리스트 원소.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthInternalUserResponse {
    private Integer userId;
    private String employeeNo;
    private String userName;
    private String userEmail;
    private String userRole;
    private String departmentName;
    private String positionName;
    private String userStatus;
}
