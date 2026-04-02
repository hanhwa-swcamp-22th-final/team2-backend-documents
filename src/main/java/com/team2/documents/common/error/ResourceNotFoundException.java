package com.team2.documents.common.error;

public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
