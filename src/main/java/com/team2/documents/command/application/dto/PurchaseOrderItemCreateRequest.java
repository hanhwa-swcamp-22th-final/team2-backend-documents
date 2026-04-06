package com.team2.documents.command.application.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Purchase Order 품목 생성 요청 DTO")
public record PurchaseOrderItemCreateRequest(
        @Schema(description = "품목 ID", example = "1")
        Integer itemId,
        @Schema(description = "품목명", example = "전자부품 A")
        String itemName,
        @Schema(description = "수량", example = "100")
        Integer quantity,
        @Schema(description = "단위", example = "EA")
        String unit,
        @Schema(description = "단가", example = "50.00")
        BigDecimal unitPrice,
        @Schema(description = "금액", example = "5000.00")
        BigDecimal amount,
        @Schema(description = "비고", example = "긴급 납품")
        String remark
) {
}
