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
public class PurchaseOrderRegistrationService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public PurchaseOrderRegistrationService(PurchaseOrderRepository purchaseOrderRepository,
                                            UserPositionRepository userPositionRepository,
                                            ApprovalRequestRepository approvalRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public void requestRegistration(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("초안 상태의 PO만 등록 요청할 수 있습니다.");
        }

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            return;
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestRepository.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.REGISTRATION,
                    userId,
                    1L
            ));
        }
    }
}
