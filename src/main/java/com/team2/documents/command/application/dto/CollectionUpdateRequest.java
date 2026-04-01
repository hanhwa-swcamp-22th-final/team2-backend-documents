package com.team2.documents.command.application.dto;

import java.time.LocalDate;

public record CollectionUpdateRequest(String status, LocalDate collectionCompletedDate, String note) {
}
