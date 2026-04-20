package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

/**
 * 결재대기 상태의 PO 에 대해 요청자 본인이 결재 요청을 취소한다. PI 와 대칭 구조.
 */
@Service
@Transactional
public class PurchaseOrderApprovalCancellationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final ApprovalDocumentMetadataService approvalDocumentMetadataService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public PurchaseOrderApprovalCancellationService(
            PurchaseOrderCommandService purchaseOrderCommandService,
            ApprovalRequestCommandService approvalRequestCommandService,
            ApprovalDocumentMetadataService approvalDocumentMetadataService,
            DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.approvalDocumentMetadataService = approvalDocumentMetadataService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void cancelApprovalRequest(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PO만 요청을 취소할 수 있습니다.");
        }

        ApprovalRequest request = approvalRequestCommandService.findPendingByDocument(
                ApprovalDocumentType.PO, poId);
        if (request.getRequesterId() == null || !request.getRequesterId().equals(userId)) {
            throw new IllegalStateException("본인이 요청한 결재만 취소할 수 있습니다.");
        }

        PurchaseOrderStatus target = ApprovalRequestType.REGISTRATION.equals(request.getRequestType())
                ? PurchaseOrderStatus.DRAFT
                : PurchaseOrderStatus.CONFIRMED;
        purchaseOrder.setStatus(target);
        purchaseOrderCommandService.save(purchaseOrder);

        approvalRequestCommandService.cancelPendingByDocument(ApprovalDocumentType.PO, poId);
        // 대시보드 결재함이 po_request_status / po_approval_status 컬럼을 필터링하므로
        // ApprovalRequest 삭제만으로는 stale 레코드가 "미지정" 으로 남는다.
        approvalDocumentMetadataService.markCancelled(ApprovalDocumentType.PO, poId);

        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "APPROVAL_CANCELLED",
                userId,
                target.name(),
                "요청자가 결재 요청을 취소하여 상태가 " + target.name() + " 로 복구되었습니다."
        );
    }
}
