package com.team2.documents.query.dto;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

public record PurchaseOrderInitialStatusResponse(PurchaseOrderStatus status) {
}
