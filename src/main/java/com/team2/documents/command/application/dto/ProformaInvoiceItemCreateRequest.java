package com.team2.documents.command.application.dto;

import java.math.BigDecimal;

public record ProformaInvoiceItemCreateRequest(
        Integer itemId,
        String itemName,
        Integer quantity,
        String unit,
        BigDecimal unitPrice,
        BigDecimal amount,
        String remark
) {
}
