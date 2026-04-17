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
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;

    public PurchaseOrderDeletionRequestService(PurchaseOrderCommandService purchaseOrderCommandService,
                                               UserPositionRepository userPositionRepository,
                                               ApprovalRequestCommandService approvalRequestCommandService,
                                               DocumentRevisionHistoryService documentRevisionHistoryService,
                                               ApproverResolver approverResolver) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
    }

    public void requestDeletion(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 삭제 요청할 수 있습니다.");
        }

        Long approverId = approverResolver.resolveApproverId(userId);
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);
        approvalRequestCommandService.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                poId,
                ApprovalRequestType.DELETION,
                userId,
                approverId
        ));
    }
}
