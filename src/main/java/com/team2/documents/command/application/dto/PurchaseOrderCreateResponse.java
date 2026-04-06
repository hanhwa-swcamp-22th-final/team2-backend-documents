package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Purchase Order 생성 응답 DTO")
public record PurchaseOrderCreateResponse(
        @Schema(description = "처리 결과 메시지", example = "PO 생성 요청이 처리되었습니다.")
        String message,
        @Schema(description = "생성된 PO 문서 ID", example = "PO-2026-0001")
        String poId) {

    public PurchaseOrderCreateResponse(String message) {
        this(message, null);
    }
}
