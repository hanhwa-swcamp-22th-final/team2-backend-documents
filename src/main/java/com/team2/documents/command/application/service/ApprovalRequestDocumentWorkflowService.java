package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@Service
@Transactional
public class ApprovalRequestDocumentWorkflowService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;
    private final DocumentAutoMailService documentAutoMailService;
    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final ProformaInvoiceModificationService proformaInvoiceModificationService;

    public ApprovalRequestDocumentWorkflowService(
            PurchaseOrderCommandService purchaseOrderCommandService,
            ProformaInvoiceCommandService proformaInvoiceCommandService,
            PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService,
            DocumentAutoMailService documentAutoMailService,
            PurchaseOrderModificationService purchaseOrderModificationService,
            ProformaInvoiceModificationService proformaInvoiceModificationService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
        this.documentAutoMailService = documentAutoMailService;
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.proformaInvoiceModificationService = proformaInvoiceModificationService;
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
        documentAutoMailService.sendApprovedPiToBuyer(proformaInvoice);
    }

    /**
     * Handles DELETION approval: performs defensive re-validation of downstream
     * documents, then soft-deletes the document. If downstream documents were
     * created while the request was pending, the approval is auto-rejected.
     *
     * @return an ApprovalRequest if the deletion was auto-rejected (caller must
     *         persist the rejection), or null if the deletion succeeded normally
     */
    public ApprovalRequest approveDeletion(ApprovalDocumentType documentType,
                                           String documentId,
                                           ApprovalRequest approvalRequest) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
                throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
            }

            // Defensive re-validation: check downstream documents
            String blockReason = purchaseOrderModificationService.checkDeletable(documentId);
            if (blockReason != null) {
                // Auto-reject: downstream documents exist
                purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
                purchaseOrderCommandService.save(purchaseOrder);
                approvalRequest.setStatus(ApprovalStatus.REJECTED);
                approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
                approvalRequest.setReason("승인 시점에 후속 문서가 존재하여 삭제가 취소되었습니다: " + blockReason);
                return approvalRequest;
            }

            purchaseOrder.setStatus(PurchaseOrderStatus.DELETED);
            purchaseOrderCommandService.save(purchaseOrder);
            return null;
        }

        // PI deletion
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 승인할 수 있습니다.");
        }

        // Defensive re-validation: check if POs reference this PI
        String blockReason = proformaInvoiceModificationService.checkDeletable(documentId);
        if (blockReason != null) {
            proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
            proformaInvoiceCommandService.save(proformaInvoice);
            approvalRequest.setStatus(ApprovalStatus.REJECTED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReason("승인 시점에 후속 문서가 존재하여 삭제가 취소되었습니다: " + blockReason);
            return approvalRequest;
        }

        proformaInvoice.setStatus(ProformaInvoiceStatus.DELETED);
        proformaInvoiceCommandService.save(proformaInvoice);
        return null;
    }

    public void rejectDocument(ApprovalDocumentType documentType, String documentId,
                               ApprovalRequestType requestType) {
        // 등록 반려 → 초안(DRAFT), 수정/삭제 반려 → 확정(CONFIRMED)
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
                throw new IllegalStateException("결재대기 상태의 PO만 반려할 수 있습니다.");
            }
            PurchaseOrderStatus rollbackStatus = ApprovalRequestType.REGISTRATION.equals(requestType)
                    ? PurchaseOrderStatus.DRAFT
                    : PurchaseOrderStatus.CONFIRMED;
            purchaseOrder.setStatus(rollbackStatus);
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        ProformaInvoiceStatus rollbackStatus = ApprovalRequestType.REGISTRATION.equals(requestType)
                ? ProformaInvoiceStatus.DRAFT
                : ProformaInvoiceStatus.CONFIRMED;
        proformaInvoice.setStatus(rollbackStatus);
        proformaInvoiceCommandService.save(proformaInvoice);
    }

    /**
     * Handles DELETION rejection: restores the document to CONFIRMED status.
     */
    public void rejectDeletion(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
        proformaInvoiceCommandService.save(proformaInvoice);
    }
}
