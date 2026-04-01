package com.team2.documents.command.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderCreateRequest(
        String poId,
        String piId,
        LocalDate issueDate,
        Integer clientId,
        Integer currencyId,
        Long managerId,
        LocalDate deliveryDate,
        String incotermsCode,
        String namedPlace,
        LocalDate sourceDeliveryDate,
        Boolean deliveryDateOverride,
        BigDecimal totalAmount,
        String clientName,
        String clientAddress,
        String country,
        String currencyCode,
        String managerName,
        Long userId,
        List<PurchaseOrderItemCreateRequest> items
) {
    public PurchaseOrderCreateRequest(Long userId) {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                userId, List.of());
    }
}
