package com.team2.documents.dto;

import com.team2.documents.entity.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequestType;

public record ApprovalRequestCreateRequest(
        ApprovalDocumentType documentType,
        String documentId,
        ApprovalRequestType requestType,
        Long requesterId,
        Long approverId,
        String comment) {
}
