package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
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
public class PurchaseOrderModificationRequestService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;
    private final DocumentOwnershipGuard documentOwnershipGuard;
    private final ObjectMapper objectMapper;

    public PurchaseOrderModificationRequestService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                   PurchaseOrderModificationService purchaseOrderModificationService,
                                                   UserPositionRepository userPositionRepository,
                                                   ApprovalRequestCommandService approvalRequestCommandService,
                                                   DocumentRevisionHistoryService documentRevisionHistoryService,
                                                   ApproverResolver approverResolver,
                                                   DocumentOwnershipGuard documentOwnershipGuard,
                                                   ObjectMapper objectMapper) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
        this.documentOwnershipGuard = documentOwnershipGuard;
        this.objectMapper = objectMapper;
    }

    public void requestModification(String poId, Long userId, PurchaseOrderCreateRequest revisedRequest) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        documentOwnershipGuard.assertCanMutate(userId, purchaseOrder.getManagerId());

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 수정 요청할 수 있습니다.");
        }
        purchaseOrderModificationService.validateModifiable(poId);
        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            // STAFF: 상태 APPROVAL_PENDING 전환 + ApprovalRequest 생성.
            purchaseOrder.setStatus(PurchaseOrderStatus.APPROVAL_PENDING);
            purchaseOrderCommandService.save(purchaseOrder);
            Long approverId = approverResolver.resolveApproverId(userId);
            approvalRequestCommandService.save(new ApprovalRequest(
                    null,
                    ApprovalDocumentType.PO,
                    poId,
                    ApprovalRequestType.MODIFICATION,
                    userId,
                    approverId,
                    null,
                    serializeRevisedRequest(revisedRequest)
            ));
            return;
        }

        // MANAGER: 상태 변경 없이 revision history 에만 기록. 실제 필드 수정은
        // PUT /purchase-orders/{id} (MANAGER 모드 updateDraft) 로 즉시 반영되므로
        // 결재 대기 상태를 거치지 않는다. 이전 동작(APPROVAL_PENDING 전환) 은
        // 팀장이 "결재 요청 취소" 를 눌렀을 때 ApprovalRequest 가 없어 404 를 유발.
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "MANAGER_MODIFY",
                userId,
                purchaseOrder.getStatus().name(),
                "팀장이 PO 를 직접 수정했습니다.",
                beforeSnapshot
        );
    }

    private String serializeRevisedRequest(PurchaseOrderCreateRequest revisedRequest) {
        if (revisedRequest == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(revisedRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("PO 수정 요청 payload 직렬화에 실패했습니다.", e);
        }
    }
}
