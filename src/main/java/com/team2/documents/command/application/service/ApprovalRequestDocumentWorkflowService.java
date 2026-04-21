package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
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
    private final PurchaseOrderCreationService purchaseOrderCreationService;
    private final ProformaInvoiceCreationService proformaInvoiceCreationService;
    private final ObjectMapper objectMapper;

    public ApprovalRequestDocumentWorkflowService(
            PurchaseOrderCommandService purchaseOrderCommandService,
            ProformaInvoiceCommandService proformaInvoiceCommandService,
            PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService,
            DocumentAutoMailService documentAutoMailService,
            PurchaseOrderModificationService purchaseOrderModificationService,
            ProformaInvoiceModificationService proformaInvoiceModificationService,
            PurchaseOrderCreationService purchaseOrderCreationService,
            ProformaInvoiceCreationService proformaInvoiceCreationService,
            ObjectMapper objectMapper) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
        this.documentAutoMailService = documentAutoMailService;
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.proformaInvoiceModificationService = proformaInvoiceModificationService;
        this.purchaseOrderCreationService = purchaseOrderCreationService;
        this.proformaInvoiceCreationService = proformaInvoiceCreationService;
        this.objectMapper = objectMapper;
    }

    public void approveDocument(ApprovalRequest approvalRequest) {
        ApprovalDocumentType documentType = approvalRequest.getDocumentType();
        String documentId = approvalRequest.getDocumentId();
        boolean modificationRequest = ApprovalRequestType.MODIFICATION.equals(approvalRequest.getRequestType());

        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
                throw new IllegalStateException("결재대기 상태의 PO만 승인할 수 있습니다.");
            }
            if (modificationRequest) {
                PurchaseOrderCreateRequest revisedRequest = readPayload(
                        approvalRequest.getReviewSnapshot(),
                        PurchaseOrderCreateRequest.class,
                        "PO 수정 요청 payload를 읽을 수 없습니다.");
                if (revisedRequest != null) {
                    purchaseOrderCreationService.applyApprovedModification(documentId, revisedRequest);
                    purchaseOrder = purchaseOrderCommandService.findById(documentId);
                }
                purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
                purchaseOrderCommandService.save(purchaseOrder);
                return;
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
        if (modificationRequest) {
            ProformaInvoiceCreateRequest revisedRequest = readPayload(
                    approvalRequest.getReviewSnapshot(),
                    ProformaInvoiceCreateRequest.class,
                    "PI 수정 요청 payload를 읽을 수 없습니다.");
            if (revisedRequest != null) {
                proformaInvoiceCreationService.applyApprovedModification(documentId, revisedRequest);
                proformaInvoice = proformaInvoiceCommandService.findById(documentId);
            }
            proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
            proformaInvoiceCommandService.save(proformaInvoice);
            return;
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
        proformaInvoiceCommandService.save(proformaInvoice);
        documentAutoMailService.sendApprovedPiToBuyer(proformaInvoice);
    }

    private <T> T readPayload(String json, Class<T> type, String errorMessage) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(errorMessage, e);
        }
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

    /**
     * Handles MODIFICATION rejection: restores the confirmed source document.
     */
    public void rejectModification(ApprovalDocumentType documentType, String documentId) {
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
