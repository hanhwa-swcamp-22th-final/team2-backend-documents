package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.repository.UserPositionRepository;

@Service
@Transactional
public class PurchaseOrderCreationService {

    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public PurchaseOrderCreationService(UserPositionRepository userPositionRepository,
                                        ApprovalRequestCommandService approvalRequestCommandService) {
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
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
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PO,
                    "TEMP-PO",
                    ApprovalRequestType.REGISTRATION,
                    userId,
                    1L
            ));
        }
    }
}
