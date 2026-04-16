package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice 삭제 응답 DTO")
public record ProformaInvoiceDeletionResponse(
        @Schema(description = "처리 결과 메시지", example = "PI 삭제 요청이 처리되었습니다.")
        String message) {
}
