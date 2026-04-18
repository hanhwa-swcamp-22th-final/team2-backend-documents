package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.command.infrastructure.client.ApproverResolver;

@Service
@Transactional
public class PurchaseOrderDeletionRequestService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;
    private final DocumentOwnershipGuard documentOwnershipGuard;

    public PurchaseOrderDeletionRequestService(PurchaseOrderCommandService purchaseOrderCommandService,
                                               PurchaseOrderModificationService purchaseOrderModificationService,
                                               UserPositionRepository userPositionRepository,
                                               ApprovalRequestCommandService approvalRequestCommandService,
                                               DocumentRevisionHistoryService documentRevisionHistoryService,
                                               ApproverResolver approverResolver,
                                               DocumentOwnershipGuard documentOwnershipGuard) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
        this.documentOwnershipGuard = documentOwnershipGuard;
    }

    /**
     * 초안 PO 를 결재 없이 바로 soft-delete 한다. DRAFT 상태만 허용.
     */
    public void deleteDraft(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        documentOwnershipGuard.assertCanMutate(userId, purchaseOrder.getManagerId());
        if (!PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("초안 상태의 PO만 직접 삭제할 수 있습니다.");
        }
        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        purchaseOrder.setStatus(PurchaseOrderStatus.DELETED);
        purchaseOrderCommandService.save(purchaseOrder);
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "DRAFT_DELETE",
                userId,
                PurchaseOrderStatus.DELETED.name(),
                "초안 PO를 삭제했습니다.",
                beforeSnapshot
        );
    }

    public void requestDeletion(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        documentOwnershipGuard.assertCanMutate(userId, purchaseOrder.getManagerId());
        purchaseOrderModificationService.validateDeletable(poId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 삭제 요청할 수 있습니다.");
        }
        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            Long approverId = approverResolver.resolveApproverId(userId);
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.DELETION,
                    userId,
                    approverId
            ));
            return;
        }

        // MANAGER: immediate soft delete
        purchaseOrder.setStatus(PurchaseOrderStatus.DELETED);
        purchaseOrderCommandService.save(purchaseOrder);

        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "DELETION_COMPLETED",
                userId,
                PurchaseOrderStatus.DELETED.name(),
                "관리자가 PO를 즉시 삭제 처리했습니다.",
                beforeSnapshot
        );
    }
}
