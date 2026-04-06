package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@Service
@Transactional
public class PurchaseOrderRejectionWorkflowService {

    private final ApprovalRequestCommandService approvalRequestCommandService;

    public PurchaseOrderRejectionWorkflowService(ApprovalRequestCommandService approvalRequestCommandService) {
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    public void reject(String poId) {
        approvalRequestCommandService.updatePendingDocument(ApprovalDocumentType.PO, poId, ApprovalStatus.REJECTED);
    }
}
