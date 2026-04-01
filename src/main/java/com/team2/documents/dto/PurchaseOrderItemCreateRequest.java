package com.team2.documents.dto;

import java.math.BigDecimal;

public record PurchaseOrderItemCreateRequest(
        Integer itemId,
        String itemName,
        Integer quantity,
        String unit,
        BigDecimal unitPrice,
        BigDecimal amount,
        String remark
) {
}
