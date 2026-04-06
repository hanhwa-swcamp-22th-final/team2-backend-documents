package com.team2.documents.command.domain.entity.enums;

import java.util.Arrays;

public enum ShipmentStatus {
    READY("preparing"),
    COMPLETED("completed");

    private final String dbValue;

    ShipmentStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ShipmentStatus fromDbValue(String dbValue) {
        return Arrays.stream(values())
                .filter(s -> s.dbValue.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ShipmentStatus dbValue: " + dbValue));
    }
}
