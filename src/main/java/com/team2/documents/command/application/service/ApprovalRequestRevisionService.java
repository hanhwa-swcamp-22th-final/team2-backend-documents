package com.team2.documents.command.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;

@Service
public class ApprovalRequestRevisionService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public ApprovalRequestRevisionService(PurchaseOrderCommandService purchaseOrderCommandService,
                                          ProformaInvoiceCommandService proformaInvoiceCommandService,
                                          DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public Map<String, Object> captureBeforeSnapshot(ApprovalRequest approvalRequest) {
        if (ApprovalDocumentType.PO.equals(approvalRequest.getDocumentType())) {
            return documentRevisionHistoryService.capturePurchaseOrderSnapshot(
                    purchaseOrderCommandService.findById(approvalRequest.getDocumentId()));
        }
        return documentRevisionHistoryService.captureProformaInvoiceSnapshot(
                proformaInvoiceCommandService.findById(approvalRequest.getDocumentId()));
    }

    public void recordRequestEvent(ApprovalRequest approvalRequest, Map<String, Object> beforeSnapshot) {
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

    public void recordReviewEvent(ApprovalRequest approvalRequest,
                                  String action,
                                  ApprovalStatus approvalStatus,
                                  String comment,
                                  Map<String, Object> beforeSnapshot) {
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
}
