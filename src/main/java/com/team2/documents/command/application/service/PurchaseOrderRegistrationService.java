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

    public PurchaseOrderRegistrationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                            UserPositionRepository userPositionRepository,
                                            ApprovalRequestCommandService approvalRequestCommandService,
                                            DocumentRevisionHistoryService documentRevisionHistoryService,
                                            PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
    }

    public void requestRegistration(String poId, Long userId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);

        if (!PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            throw new BusinessConflictException("초안 상태의 PO만 등록 요청할 수 있습니다.");
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
        purchaseOrderCommandService.save(purchaseOrder);
        approvalRequestCommandService.save(new ApprovalRequest(
                ApprovalDocumentType.PO,
                poId,
                ApprovalRequestType.REGISTRATION,
                userId,
                1L
        ));
    }
}
