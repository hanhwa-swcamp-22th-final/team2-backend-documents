package com.team2.documents.dto;

import com.team2.documents.entity.enums.ApprovalStatus;

public record ApprovalRequestUpdateRequest(ApprovalStatus status, String comment) {
}
