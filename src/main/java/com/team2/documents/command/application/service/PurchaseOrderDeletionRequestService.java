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

@Service
@Transactional
public class PurchaseOrderDeletionRequestService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public PurchaseOrderDeletionRequestService(PurchaseOrderCommandService purchaseOrderCommandService,
                                               UserPositionRepository userPositionRepository,
                                               ApprovalRequestCommandService approvalRequestCommandService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    public void requestDeletion(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 삭제 요청할 수 있습니다.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.DELETION,
                    userId,
                    1L
            ));
        }
    }
}
