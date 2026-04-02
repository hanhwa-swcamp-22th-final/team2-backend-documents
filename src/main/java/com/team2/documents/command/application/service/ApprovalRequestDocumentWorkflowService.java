package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;

@Service
public class ApprovalRequestDocumentWorkflowService {

    private final PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;
    private final PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;
    private final ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;
    private final ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    public ApprovalRequestDocumentWorkflowService(
            PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService,
            PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService,
            ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService,
            ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService) {
        this.purchaseOrderApprovalWorkflowService = purchaseOrderApprovalWorkflowService;
        this.purchaseOrderRejectionWorkflowService = purchaseOrderRejectionWorkflowService;
        this.proformaInvoiceApprovalWorkflowService = proformaInvoiceApprovalWorkflowService;
        this.proformaInvoiceRejectionWorkflowService = proformaInvoiceRejectionWorkflowService;
    }

    public void approveDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            purchaseOrderApprovalWorkflowService.approve(documentId);
            return;
        }
        proformaInvoiceApprovalWorkflowService.approve(documentId);
    }

    public void rejectDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            purchaseOrderRejectionWorkflowService.reject(documentId);
            return;
        }
        proformaInvoiceRejectionWorkflowService.reject(documentId);
    }
}
