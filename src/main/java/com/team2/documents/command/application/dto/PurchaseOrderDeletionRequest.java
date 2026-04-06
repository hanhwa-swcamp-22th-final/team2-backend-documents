package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Purchase Order 삭제 요청 DTO")
public record PurchaseOrderDeletionRequest(
        @Schema(description = "PO 문서 ID", example = "PO-2026-0001")
        String poId,
        @Schema(description = "요청 사용자 ID", example = "1")
        Long userId) {
}
