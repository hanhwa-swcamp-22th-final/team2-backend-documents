package com.team2.documents.command.application.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수금 상태 변경 요청 DTO")
public record CollectionUpdateRequest(
        @Schema(description = "수금 상태", example = "COMPLETED")
        String status,
        @Schema(description = "수금 완료일", example = "2026-04-06")
        LocalDate collectionCompletedDate,
        @Schema(description = "비고", example = "수금 완료")
        String note) {
}
