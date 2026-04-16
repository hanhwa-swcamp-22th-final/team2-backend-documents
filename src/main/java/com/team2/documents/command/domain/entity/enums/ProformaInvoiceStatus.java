package com.team2.documents.command.domain.entity.enums;

import java.util.Arrays;

public enum ProformaInvoiceStatus {
    DRAFT("draft"),
    CONFIRMED("confirmed"),
    APPROVAL_PENDING("pending_approval"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    DELETION_REQUESTED("deletion_requested"),
    REGISTRATION_REQUESTED("registration_requested"),
    MODIFICATION_REQUESTED("modification_requested"),
    DELETED("deleted");

    private final String dbValue;

    ProformaInvoiceStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProformaInvoiceStatus fromDbValue(String dbValue) {
        return Arrays.stream(values())
                .filter(s -> s.dbValue.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ProformaInvoiceStatus dbValue: " + dbValue));
    }
}
