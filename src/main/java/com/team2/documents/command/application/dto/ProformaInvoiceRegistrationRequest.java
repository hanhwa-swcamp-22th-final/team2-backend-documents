package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice 등록 요청 DTO")
public record ProformaInvoiceRegistrationRequest(
        @Schema(description = "PI 문서 ID", example = "PI-2026-0001")
        String piId,
        @Schema(description = "요청 사용자 ID", example = "1")
        Long userId) {
}
