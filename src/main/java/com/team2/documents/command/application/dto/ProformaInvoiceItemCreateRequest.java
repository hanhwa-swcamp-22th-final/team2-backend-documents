package com.team2.documents.command.application.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice 품목 생성 요청 DTO")
public record ProformaInvoiceItemCreateRequest(
        @Schema(description = "품목 ID", example = "1")
        Integer itemId,
        @Schema(description = "품목명", example = "전자부품 A")
        String itemName,
        @Schema(description = "수량", example = "100")
        Integer quantity,
        @Schema(description = "단위", example = "EA")
        String unit,
        @Schema(description = "단가. 외화 문서여도 프론트에서는 KRW 기준 단가를 전달합니다.", example = "50.00")
        BigDecimal unitPrice,
        @Schema(description = "금액. 외화 문서여도 프론트에서는 KRW 기준 금액을 전달합니다. null이면 단가 * 수량으로 계산합니다.", example = "5000.00")
        BigDecimal amount,
        @Schema(description = "비고", example = "긴급 납품")
        String remark
) {
}
