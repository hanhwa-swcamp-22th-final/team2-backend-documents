package com.team2.documents.command.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@Service
@Transactional
public class ApprovalDocumentMetadataService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserSnapshotService userSnapshotService;

    public ApprovalDocumentMetadataService(PurchaseOrderCommandService purchaseOrderCommandService,
                                           ProformaInvoiceCommandService proformaInvoiceCommandService,
                                           UserSnapshotService userSnapshotService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userSnapshotService = userSnapshotService;
    }

    public void markRequested(ApprovalRequest approvalRequest) {
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(approvalRequest.getDocumentId());
            purchaseOrder.setApprovalStatus("대기");
            purchaseOrder.setRequestStatus(toRequestStatus(approvalRequest.getRequestType()));
            purchaseOrder.setApprovalAction(toApprovalAction(approvalRequest.getRequestType()));
            purchaseOrder.setApprovalRequestedBy(userSnapshotService.resolveRequesterDisplayName(approvalRequest.getRequesterId()));
            purchaseOrder.setApprovalRequestedAt(approvalRequest.getRequestedAt());
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(approvalRequest.getDocumentId());
        proformaInvoice.setApprovalStatus("대기");
        proformaInvoice.setRequestStatus(toRequestStatus(approvalRequest.getRequestType()));
        proformaInvoice.setApprovalAction(toApprovalAction(approvalRequest.getRequestType()));
        proformaInvoice.setApprovalRequestedBy(userSnapshotService.resolveRequesterDisplayName(approvalRequest.getRequesterId()));
        proformaInvoice.setApprovalRequestedAt(approvalRequest.getRequestedAt());
        proformaInvoiceCommandService.save(proformaInvoice);
    }

    /**
     * 요청자가 PENDING 결재를 취소했을 때 호출. markRequested 가 문서 테이블에 기록한
     * approval_status / request_status / approval_action / approval_requested_by /
     * approval_requested_at 컬럼을 전부 null 로 되돌려, 대시보드 결재함(문서 컬럼 기반
     * 필터) 에서 stale 엔트리("미지정" 으로 남는 레코드) 가 노출되지 않도록 한다.
     * ApprovalRequest 엔티티 자체는 호출부(ApprovalRequestCommandService.cancelPendingByDocument)
     * 에서 이미 삭제된다.
     */
    public void markCancelled(ApprovalDocumentType documentType, String documentId) {
        if (ApprovalDocumentType.PO.equals(documentType)) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(documentId);
            purchaseOrder.setApprovalStatus(null);
            purchaseOrder.setRequestStatus(null);
            purchaseOrder.setApprovalAction(null);
            purchaseOrder.setApprovalRequestedBy(null);
            purchaseOrder.setApprovalRequestedAt(null);
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(documentId);
        proformaInvoice.setApprovalStatus(null);
        proformaInvoice.setRequestStatus(null);
        proformaInvoice.setApprovalAction(null);
        proformaInvoice.setApprovalRequestedBy(null);
        proformaInvoice.setApprovalRequestedAt(null);
        proformaInvoiceCommandService.save(proformaInvoice);
    }

    public void markReviewed(ApprovalRequest approvalRequest, ApprovalStatus approvalStatus, String comment) {
        String statusText = ApprovalStatus.APPROVED.equals(approvalStatus) ? "승인" : "반려";
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(approvalRequest.getDocumentId());
            purchaseOrder.setApprovalStatus(statusText);
            purchaseOrder.setApprovalReview(comment);
            if (purchaseOrder.getApprovalRequestedAt() == null) {
                purchaseOrder.setApprovalRequestedAt(LocalDateTime.now());
            }
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(approvalRequest.getDocumentId());
        proformaInvoice.setApprovalStatus(statusText);
        proformaInvoice.setApprovalReview(comment);
        if (proformaInvoice.getApprovalRequestedAt() == null) {
            proformaInvoice.setApprovalRequestedAt(LocalDateTime.now());
        }
        proformaInvoiceCommandService.save(proformaInvoice);
    }

    private String toRequestStatus(ApprovalRequestType requestType) {
        return switch (requestType) {
            case REGISTRATION -> "등록요청";
            case MODIFICATION -> "수정요청";
            case DELETION -> "삭제요청";
        };
    }

    private String toApprovalAction(ApprovalRequestType requestType) {
        return switch (requestType) {
            case REGISTRATION -> "등록";
            case MODIFICATION -> "수정";
            case DELETION -> "삭제";
        };
    }
}
