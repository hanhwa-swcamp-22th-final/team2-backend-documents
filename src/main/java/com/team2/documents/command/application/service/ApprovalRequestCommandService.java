package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.repository.ApprovalRequestRepository;

@Service
@Transactional
public class ApprovalRequestCommandService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;
    private final PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;
    private final ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;
    private final ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;
    private final ApprovalDocumentMetadataService approvalDocumentMetadataService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public ApprovalRequestCommandService(ApprovalRequestRepository approvalRequestRepository,
                                         PurchaseOrderCommandService purchaseOrderCommandService,
                                         ProformaInvoiceCommandService proformaInvoiceCommandService,
                                         PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService,
                                         PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService,
                                         ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService,
                                         ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService,
                                         ApprovalDocumentMetadataService approvalDocumentMetadataService,
                                         DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.purchaseOrderApprovalWorkflowService = purchaseOrderApprovalWorkflowService;
        this.purchaseOrderRejectionWorkflowService = purchaseOrderRejectionWorkflowService;
        this.proformaInvoiceApprovalWorkflowService = proformaInvoiceApprovalWorkflowService;
        this.proformaInvoiceRejectionWorkflowService = proformaInvoiceRejectionWorkflowService;
        this.approvalDocumentMetadataService = approvalDocumentMetadataService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public ApprovalRequest findById(Long approvalRequestId) {
        return approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다."));
    }

    public ApprovalRequest findPendingByDocument(ApprovalDocumentType documentType, String documentId) {
        return approvalRequestRepository.findPendingByDocument(documentType, documentId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."));
    }

    public ApprovalRequest save(ApprovalRequest approvalRequest) {
        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);
        java.util.Map<String, Object> beforeSnapshot = captureBeforeSnapshot(saved);
        approvalDocumentMetadataService.markRequested(saved);
        recordRequestEvent(saved, beforeSnapshot);
        return saved;
    }

    public ApprovalRequest create(ApprovalRequestCreateRequest request) {
        ApprovalRequest approvalRequest = new ApprovalRequest(
                request.documentType(),
                request.documentId(),
                request.requestType(),
                request.requesterId(),
                request.approverId(),
                request.comment()
        );
        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);
        java.util.Map<String, Object> beforeSnapshot = captureBeforeSnapshot(saved);
        approvalDocumentMetadataService.markRequested(saved);
        recordRequestEvent(saved, beforeSnapshot);
        return saved;
    }

    public ApprovalRequest update(Long approvalRequestId, ApprovalStatus targetApprovalStatus) {
        return update(approvalRequestId, targetApprovalStatus, null);
    }

    public ApprovalRequest update(Long approvalRequestId, ApprovalStatus targetApprovalStatus, String comment) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다."));

        if (ApprovalStatus.APPROVED.equals(targetApprovalStatus)) {
            approveDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            approvalRequest.setStatus(ApprovalStatus.APPROVED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReviewSnapshot(comment);
            approvalRequestRepository.save(approvalRequest);
            java.util.Map<String, Object> beforeSnapshot = captureBeforeSnapshot(approvalRequest);
            approvalDocumentMetadataService.markReviewed(approvalRequest, targetApprovalStatus, comment);
            recordReviewEvent(approvalRequest, "REVIEW_APPROVED", targetApprovalStatus, comment, beforeSnapshot);
            return approvalRequest;
        }

        if (ApprovalStatus.REJECTED.equals(targetApprovalStatus)) {
            rejectDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            approvalRequest.setStatus(ApprovalStatus.REJECTED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReviewSnapshot(comment);
            approvalRequestRepository.save(approvalRequest);
            java.util.Map<String, Object> beforeSnapshot = captureBeforeSnapshot(approvalRequest);
            approvalDocumentMetadataService.markReviewed(approvalRequest, targetApprovalStatus, comment);
            recordReviewEvent(approvalRequest, "REVIEW_REJECTED", targetApprovalStatus, comment, beforeSnapshot);
            return approvalRequest;
        }

        throw new IllegalArgumentException("승인 또는 반려 상태만 처리할 수 있습니다.");
    }

    private void approveDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            purchaseOrderApprovalWorkflowService.approve(documentId);
            return;
        }
        proformaInvoiceApprovalWorkflowService.approve(documentId);
    }

    private void rejectDocument(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            purchaseOrderRejectionWorkflowService.reject(documentId);
            return;
        }
        proformaInvoiceRejectionWorkflowService.reject(documentId);
    }

    private void recordRequestEvent(ApprovalRequest approvalRequest, java.util.Map<String, Object> beforeSnapshot) {
        String action = switch (approvalRequest.getRequestType()) {
            case REGISTRATION -> "REQUEST_REGISTRATION";
            case MODIFICATION -> "REQUEST_MODIFICATION";
            case DELETION -> "REQUEST_DELETION";
        };
        String message = switch (approvalRequest.getRequestType()) {
            case REGISTRATION -> "등록 결재를 요청했습니다.";
            case MODIFICATION -> "수정 결재를 요청했습니다.";
            case DELETION -> "삭제 결재를 요청했습니다.";
        };
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            documentRevisionHistoryService.recordPurchaseOrderEvent(
                    approvalRequest.getDocumentId(),
                    action,
                    approvalRequest.getRequesterId(),
                    "APPROVAL_PENDING",
                    message,
                    beforeSnapshot
            );
            return;
        }
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                approvalRequest.getDocumentId(),
                action,
                approvalRequest.getRequesterId(),
                "APPROVAL_PENDING",
                message,
                beforeSnapshot
        );
    }

    private void recordReviewEvent(ApprovalRequest approvalRequest,
                                   String action,
                                   ApprovalStatus approvalStatus,
                                   String comment,
                                   java.util.Map<String, Object> beforeSnapshot) {
        String status = approvalStatus.name();
        String message = comment == null || comment.isBlank()
                ? "결재 요청을 처리했습니다."
                : comment;
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            documentRevisionHistoryService.recordPurchaseOrderEvent(
                    approvalRequest.getDocumentId(),
                    action,
                    approvalRequest.getApproverId(),
                    status,
                    message,
                    beforeSnapshot
            );
            return;
        }
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                approvalRequest.getDocumentId(),
                action,
                approvalRequest.getApproverId(),
                status,
                message,
                beforeSnapshot
        );
    }

    private java.util.Map<String, Object> captureBeforeSnapshot(ApprovalRequest approvalRequest) {
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            return documentRevisionHistoryService.capturePurchaseOrderSnapshot(
                    purchaseOrderCommandService.findById(approvalRequest.getDocumentId()));
        }
        return documentRevisionHistoryService.captureProformaInvoiceSnapshot(
                proformaInvoiceCommandService.findById(approvalRequest.getDocumentId()));
    }
}
