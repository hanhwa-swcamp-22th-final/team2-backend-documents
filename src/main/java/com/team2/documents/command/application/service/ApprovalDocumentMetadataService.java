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
