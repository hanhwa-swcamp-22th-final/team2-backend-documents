package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
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

        purchaseOrder.requestModification();

        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestRepository.createForPurchaseOrder(userId);
        }
    }
}
