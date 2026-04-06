package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결재 요청 상태 변경 요청 DTO")
public record ApprovalRequestUpdateRequest(
        @Schema(description = "변경할 결재 상태", example = "APPROVED")
        ApprovalStatus status,
        @Schema(description = "결재 코멘트", example = "승인합니다.")
        String comment) {
}
