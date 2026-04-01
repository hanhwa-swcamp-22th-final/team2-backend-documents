package com.team2.documents.query.dto;

public record PurchaseOrderCreateResponse(String message, String poId) {

    public PurchaseOrderCreateResponse(String message) {
        this(message, null);
    }
}
