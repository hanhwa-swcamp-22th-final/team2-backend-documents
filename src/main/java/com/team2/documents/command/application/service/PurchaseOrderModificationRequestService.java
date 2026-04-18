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
public class PurchaseOrderModificationRequestService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;
    private final DocumentOwnershipGuard documentOwnershipGuard;

    public PurchaseOrderModificationRequestService(PurchaseOrderCommandService purchaseOrderCommandService,
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

    public void requestModification(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        documentOwnershipGuard.assertCanMutate(userId, purchaseOrder.getManagerId());

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 수정 요청할 수 있습니다.");
        }
        purchaseOrderModificationService.validateModifiable(poId);
        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            Long approverId = approverResolver.resolveApproverId(userId);
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.MODIFICATION,
                    userId,
                    approverId
            ));
            return;
        }

        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "REQUEST_MODIFICATION",
                userId,
                PurchaseOrderStatus.APPROVAL_PENDING.name(),
                "관리자가 PO 수정을 요청했습니다.",
                beforeSnapshot
        );
    }
}
