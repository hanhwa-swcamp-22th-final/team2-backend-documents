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
    private final PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;
    private final PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;
    private final ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;
    private final ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    public ApprovalRequestCommandService(ApprovalRequestRepository approvalRequestRepository,
                                         PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService,
                                         PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService,
                                         ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService,
                                         ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.purchaseOrderApprovalWorkflowService = purchaseOrderApprovalWorkflowService;
        this.purchaseOrderRejectionWorkflowService = purchaseOrderRejectionWorkflowService;
        this.proformaInvoiceApprovalWorkflowService = proformaInvoiceApprovalWorkflowService;
        this.proformaInvoiceRejectionWorkflowService = proformaInvoiceRejectionWorkflowService;
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
        return approvalRequestRepository.save(approvalRequest);
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
        return approvalRequestRepository.save(approvalRequest);
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
            return approvalRequest;
        }

        if (ApprovalStatus.REJECTED.equals(targetApprovalStatus)) {
            rejectDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            approvalRequest.setStatus(ApprovalStatus.REJECTED);
            approvalRequest.setReviewedAt(java.time.LocalDateTime.now());
            approvalRequest.setReviewSnapshot(comment);
            approvalRequestRepository.save(approvalRequest);
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
}
