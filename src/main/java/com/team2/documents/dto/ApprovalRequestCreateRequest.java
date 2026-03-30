package com.team2.documents.dto;

import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;

public record ApprovalRequestCreateRequest(
        ApprovalDocumentType documentType,
        String documentId,
        ApprovalRequestType requestType,
        Long requesterId,
        Long approverId,
        String comment) {
}
