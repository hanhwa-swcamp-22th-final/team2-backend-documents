package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice 생성 응답 DTO")
public record ProformaInvoiceCreateResponse(
        @Schema(description = "처리 결과 메시지", example = "PI 생성 요청이 처리되었습니다.")
        String message,
        @Schema(description = "생성된 PI 문서 ID", example = "PI-2026-0001")
        String piId) {
}
