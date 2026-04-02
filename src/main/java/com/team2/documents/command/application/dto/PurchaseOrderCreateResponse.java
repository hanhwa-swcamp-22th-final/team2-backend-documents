package com.team2.documents.command.application.dto;

public record PurchaseOrderCreateResponse(String message, String poId) {

    public PurchaseOrderCreateResponse(String message) {
        this(message, null);
    }
}
