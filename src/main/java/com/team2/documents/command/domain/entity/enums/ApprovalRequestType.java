package com.team2.documents.command.domain.entity.enums;

import java.util.Arrays;

public enum ApprovalRequestType {
    REGISTRATION("registration"),
    MODIFICATION("modification"),
    DELETION("deletion");

    private final String dbValue;

    ApprovalRequestType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ApprovalRequestType fromDbValue(String dbValue) {
        return Arrays.stream(values())
                .filter(s -> s.dbValue.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApprovalRequestType dbValue: " + dbValue));
    }
}
