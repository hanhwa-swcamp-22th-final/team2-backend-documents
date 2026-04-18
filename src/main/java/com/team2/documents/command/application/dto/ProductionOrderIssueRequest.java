package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "생산지시서 발행 요청")
public record ProductionOrderIssueRequest(
        @Schema(description = "생산담당자 userId. null 이면 PO 의 영업담당자 승계.", example = "12")
        Long assigneeUserId,
        @Schema(description = "생산담당자 표시명 (프론트 표시용, 없으면 백엔드에서 PO managerName 사용)")
        String assigneeName
) {
}
