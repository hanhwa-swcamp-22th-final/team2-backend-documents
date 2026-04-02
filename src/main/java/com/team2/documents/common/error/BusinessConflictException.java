package com.team2.documents.common.error;

public class BusinessConflictException extends IllegalStateException {

    public BusinessConflictException(String message) {
        super(message);
    }
}
