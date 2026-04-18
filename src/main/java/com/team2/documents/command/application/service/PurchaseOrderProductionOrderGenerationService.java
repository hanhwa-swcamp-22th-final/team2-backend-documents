package com.team2.documents.command.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.command.infrastructure.client.AuthInternalUserResponse;

@Service
@Transactional
public class PurchaseOrderProductionOrderGenerationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProductionOrderCommandService productionOrderCommandService;
    private final ProductionOrderRepository productionOrderRepository;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentLinkService documentLinkService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final DocumentAutoMailService documentAutoMailService;
    private final AuthFeignClient authFeignClient;

    public PurchaseOrderProductionOrderGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                         ProductionOrderCommandService productionOrderCommandService,
                                                         ProductionOrderRepository productionOrderRepository,
                                                         DocumentNumberGeneratorService documentNumberGeneratorService,
                                                         DocumentLinkService documentLinkService,
                                                         DocumentRevisionHistoryService documentRevisionHistoryService,
                                                         DocumentAutoMailService documentAutoMailService,
                                                         AuthFeignClient authFeignClient) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.productionOrderCommandService = productionOrderCommandService;
        this.productionOrderRepository = productionOrderRepository;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.documentAutoMailService = documentAutoMailService;
        this.authFeignClient = authFeignClient;
    }

    public void generate(String poId) {
        generate(poId, null, null, null);
    }

    public void generate(String poId, Long assigneeUserId, String assigneeName) {
        generate(poId, assigneeUserId, assigneeName, null);
    }

    /**
     * 생산지시서 발행.
     *
     * 규칙:
     * - 상태는 확정(CONFIRMED) 만 허용.
     * - PO 당 한 건만 발행 (countByPoId 사전 체크). 중복 발행하면 후속 email-send 가
     *   IncorrectResultSizeDataAccessException 으로 깨지므로 idempotent 가드.
     * - assigneeUserId 가 null 이면 담당자 미지정으로 저장한다. 이 경우 complete 는
     *   PRODUCTION role 체크만으로 허용 (role 가드는 컨트롤러 @PreAuthorize).
     * - assigneeUserId 가 non-null 이면 Auth 서비스의 생산 role 활성 사용자 리스트에
     *   실제 존재하는지 검증. 이름은 Auth 응답의 userName 으로 재도출 (클라이언트
     *   assigneeName 은 힌트 용도일 뿐 — 서버 값을 원칙으로 저장).
     * - 감사 기록의 actor 는 발행자(callerUserId). null 이면 fallback 으로 PO managerId.
     */
    public void generate(String poId, Long assigneeUserId, String assigneeName, Long callerUserId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 선택 생성 문서를 가질 수 있습니다.");
        }
        if (productionOrderRepository.countByPoId(purchaseOrder.getPurchaseOrderId()) > 0) {
            throw new IllegalStateException("이미 발행된 생산지시서가 있습니다.");
        }

        Long effectiveManagerId = null;
        String effectiveManagerName = null;
        if (assigneeUserId != null) {
            AuthInternalUserResponse resolved = resolveProductionAssignee(assigneeUserId);
            effectiveManagerId = assigneeUserId;
            effectiveManagerName = resolved.getUserName();
        }

        String productionOrderId = documentNumberGeneratorService.nextProductionOrderId();
        ProductionOrder productionOrder = new ProductionOrder(
                productionOrderId,
                purchaseOrder.getPurchaseOrderId(),
                poId,
                java.time.LocalDate.now(),
                purchaseOrder.getClientId(),
                effectiveManagerId,
                purchaseOrder.getDeliveryDate(),
                "진행중",
                purchaseOrder.getClientName(),
                purchaseOrder.getCountry(),
                effectiveManagerName,
                purchaseOrder.getItems().isEmpty() ? null : purchaseOrder.getItems().get(0).getItemName(),
                "[{\"id\":\"" + poId + "\",\"type\":\"PO\",\"status\":\"" + purchaseOrder.getStatus().name() + "\"}]",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
        productionOrderCommandService.save(productionOrder);
        documentLinkService.linkProductionOrder(poId, productionOrderId);
        Long actorUserId = callerUserId != null ? callerUserId : purchaseOrder.getManagerId();
        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "GENERATE_PRODUCTION_ORDER",
                actorUserId,
                purchaseOrder.getStatus().name(),
                "생산지시서를 발행했습니다."
                        + (assigneeUserId != null ? " (담당자 userId=" + assigneeUserId + ")" : " (담당자 미지정)")
        );
        documentAutoMailService.sendProductionOrderToProductionTeam(purchaseOrder, productionOrder);
    }

    /**
     * Auth 서비스의 생산 role 활성 사용자 목록에서 userId 존재 여부를 확인하고
     * 해당 사용자의 공식 userName 을 반환한다. 누락/비활성/다른 role 이면 예외.
     */
    private AuthInternalUserResponse resolveProductionAssignee(Long assigneeUserId) {
        List<AuthInternalUserResponse> candidates = authFeignClient.getUsersByRole("production", "active");
        if (candidates == null) candidates = List.of();
        return candidates.stream()
                .filter(u -> u.getUserId() != null && Objects.equals(assigneeUserId, u.getUserId().longValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "지정된 담당자(userId=" + assigneeUserId + ")는 활성 생산 사용자가 아닙니다."));
    }
}
