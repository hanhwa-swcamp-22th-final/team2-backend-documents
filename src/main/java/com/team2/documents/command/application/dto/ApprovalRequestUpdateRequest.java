package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

public record ApprovalRequestUpdateRequest(ApprovalStatus status, String comment) {
}
