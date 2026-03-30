package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;
import com.team2.documents.repository.UserPositionRepository;

@Service
public class PurchaseOrderModificationRequestService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public PurchaseOrderModificationRequestService(PurchaseOrderRepository purchaseOrderRepository,
                                                   UserPositionRepository userPositionRepository,
                                                   ApprovalRequestRepository approvalRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public void requestModification(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 수정 요청할 수 있습니다.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestRepository.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.MODIFICATION,
                    userId,
                    1L
            ));
        }
    }
}
