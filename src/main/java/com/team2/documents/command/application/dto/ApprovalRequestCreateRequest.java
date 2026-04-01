package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;

public record ApprovalRequestCreateRequest(
        ApprovalDocumentType documentType,
        String documentId,
        ApprovalRequestType requestType,
        Long requesterId,
        Long approverId,
        String comment) {
}
