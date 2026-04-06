package com.team2.documents.command.domain.entity.enums;

import java.util.Arrays;

public enum ApprovalStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String dbValue;

    ApprovalStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ApprovalStatus fromDbValue(String dbValue) {
        return Arrays.stream(values())
                .filter(s -> s.dbValue.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApprovalStatus dbValue: " + dbValue));
    }
}
