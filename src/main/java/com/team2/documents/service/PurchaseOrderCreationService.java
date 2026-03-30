package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.UserPositionRepository;

@Service
public class PurchaseOrderCreationService {

    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public PurchaseOrderCreationService(UserPositionRepository userPositionRepository,
                                        ApprovalRequestRepository approvalRequestRepository) {
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public PurchaseOrderStatus determineInitialStatus(Long userId) {
        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            return PurchaseOrderStatus.CONFIRMED;
        }
        return PurchaseOrderStatus.APPROVAL_PENDING;
    }

    public void create(Long userId) {
        PurchaseOrderStatus initialStatus = determineInitialStatus(userId);
        if (PurchaseOrderStatus.APPROVAL_PENDING.equals(initialStatus)) {
            approvalRequestRepository.createForPurchaseOrder(userId);
        }
    }
}
