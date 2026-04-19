package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice 등록 요청 DTO")
public record ProformaInvoiceRegistrationRequest(
        @Schema(description = "PI 문서 ID", example = "PI-2026-0001")
        String piId,
        @Schema(description = "요청 사용자 ID", example = "1")
        Long userId,
        @Schema(description = "프론트에서 선택한 결재자 ID (선택). null 이면 서버가 요청자 팀장 자동 선택.", example = "3")
        Long approverId) {
}
