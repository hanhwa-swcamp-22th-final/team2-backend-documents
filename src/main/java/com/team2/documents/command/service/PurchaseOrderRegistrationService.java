package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.repository.UserPositionRepository;

@Service
@Transactional
public class PurchaseOrderRegistrationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public PurchaseOrderRegistrationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                            UserPositionRepository userPositionRepository,
                                            ApprovalRequestCommandService approvalRequestCommandService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    public void requestRegistration(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("초안 상태의 PO만 등록 요청할 수 있습니다.");
        }

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderCommandService.save(purchaseOrder);
            return;
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);
        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.REGISTRATION,
                    userId,
                    1L
            ));
        }
    }
}
