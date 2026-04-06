package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class ApprovalRequestDocumentWorkflowService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    public ApprovalRequestDocumentWorkflowService(
            PurchaseOrderCommandService purchaseOrderCommandService,
            ProformaInvoiceCommandService proformaInvoiceCommandService,
            PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
    }

    public void approveDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
                throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
            }
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderCommandService.save(purchaseOrder);
            purchaseOrderDocumentGenerationService.generateOnConfirmation(documentId);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 승인할 수 있습니다.");
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
        proformaInvoiceCommandService.save(proformaInvoice);
    }

    public void rejectDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
                throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
            }
            purchaseOrder.setStatus(PurchaseOrderStatus.REJECTED);
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.REJECTED);
        proformaInvoiceCommandService.save(proformaInvoice);
    }
}
