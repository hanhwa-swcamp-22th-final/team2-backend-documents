package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.dto.ApprovalRequestCreateRequest;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.repository.ApprovalRequestRepository;

@Service
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
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다."));

        if (ApprovalStatus.APPROVED.equals(targetApprovalStatus)) {
            approveDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
            return approvalRequest;
        }

        if (ApprovalStatus.REJECTED.equals(targetApprovalStatus)) {
            rejectDocument(approvalRequest.getDocumentType(), approvalRequest.getDocumentId());
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
