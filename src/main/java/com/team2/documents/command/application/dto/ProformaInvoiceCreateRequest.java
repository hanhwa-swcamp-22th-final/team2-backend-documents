package com.team2.documents.command.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proforma Invoice(견적송장) 생성 요청 DTO")
public record ProformaInvoiceCreateRequest(
        @Schema(description = "PI 문서 ID", example = "PI-2026-0001")
        String piId,
        @Schema(description = "발행일", example = "2026-04-06")
        LocalDate issueDate,
        @Schema(description = "거래처 ID", example = "1")
        Integer clientId,
        @Schema(description = "통화 ID", example = "1")
        Integer currencyId,
        @Schema(description = "담당자 ID", example = "1")
        Long managerId,
        @Schema(description = "납기일", example = "2026-05-06")
        LocalDate deliveryDate,
        @Schema(description = "인코텀즈 코드", example = "FOB")
        String incotermsCode,
        @Schema(description = "지정 장소", example = "부산항")
        String namedPlace,
        @Schema(description = "총 금액", example = "10000.00")
        BigDecimal totalAmount,
        @Schema(description = "거래처명", example = "ABC Trading Co.")
        String clientName,
        @Schema(description = "거래처 주소", example = "123 Main St, New York")
        String clientAddress,
        @Schema(description = "국가", example = "USA")
        String country,
        @Schema(description = "통화 코드", example = "USD")
        String currencyCode,
        @Schema(description = "담당자명", example = "홍길동")
        String managerName,
        @Schema(description = "요청 사용자 ID", example = "1")
        Long userId,
        @Schema(description = "PI 품목 목록")
        List<ProformaInvoiceItemCreateRequest> items
) {
}
