package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Purchase Order 등록 응답 DTO")
public record PurchaseOrderRegistrationResponse(
        @Schema(description = "처리 결과 메시지", example = "PO 등록 요청이 처리되었습니다.")
        String message) {
}
