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
import com.team2.documents.common.error.BusinessConflictException;
import com.team2.documents.common.error.ResourceNotFoundException;

@Service
@Transactional
public class PurchaseOrderRegistrationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;
    private final ApproverResolver approverResolver;

    public PurchaseOrderRegistrationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                            UserPositionRepository userPositionRepository,
                                            ApprovalRequestCommandService approvalRequestCommandService,
                                            DocumentRevisionHistoryService documentRevisionHistoryService,
                                            PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService,
                                            ApproverResolver approverResolver) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
        this.approverResolver = approverResolver;
    }

    public void requestRegistration(String poId, Long userId) {
        requestRegistration(poId, userId, null);
    }

    public void requestRegistration(String poId, Long userId, Long approverIdOverride) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            throw new BusinessConflictException("초안 상태의 PO만 등록 요청할 수 있습니다.");
        }

        java.util.Map<String, Object> beforeSnapshot = documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);
        if (PositionLevel.MANAGER.equals(positionLevel)) {
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrderCommandService.save(purchaseOrder);
            purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
            documentRevisionHistoryService.recordPurchaseOrderEvent(
                    poId,
                    "REQUEST_REGISTRATION",
                    userId,
                    PurchaseOrderStatus.CONFIRMED.name(),
                    "관리자가 PO를 즉시 확정했습니다.",
                    beforeSnapshot
            );
            return;
        }

        Long approverId = approverIdOverride != null
                ? approverIdOverride
                : approverResolver.resolveApproverId(userId);

        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);
        approvalRequestCommandService.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                poId,
                ApprovalRequestType.REGISTRATION,
                userId,
                approverId
        ));
    }
}
