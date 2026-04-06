package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@Service
@Transactional
public class PurchaseOrderApprovalWorkflowService {

    private final ApprovalRequestCommandService approvalRequestCommandService;

    public PurchaseOrderApprovalWorkflowService(ApprovalRequestCommandService approvalRequestCommandService) {
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    public void approve(String poId) {
        approvalRequestCommandService.updatePendingDocument(ApprovalDocumentType.PO, poId, ApprovalStatus.APPROVED);
    }
}
