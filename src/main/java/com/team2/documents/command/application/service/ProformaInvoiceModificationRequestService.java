package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.command.infrastructure.client.ApproverResolver;

/**
 * 확정(CONFIRMED) 상태의 PI 에 대한 수정 요청. PO 의
 * {@link PurchaseOrderModificationRequestService} 와 대칭 구조.
 *
 * - STAFF: 상태를 APPROVAL_PENDING 으로 이동하고 ApprovalRequest(MODIFICATION) 생성.
 * - MANAGER(팀장/ADMIN): 상태를 **변경하지 않고** (CONFIRMED 유지) revision history 만
 *   기록. 이후 실제 필드 수정은 팀장 전용 PUT /proforma-invoices/{piId} (MANAGER
 *   모드 updateDraft) 로 직접 반영되므로 결재 경로를 밟지 않는다. 기존엔 MANAGER 도
 *   APPROVAL_PENDING 으로 밀어 대시보드 미노출 + "결재 요청 취소" 404 오류(G3+G4)
 *   를 유발했음.
 */
@Service
@Transactional
public class ProformaInvoiceModificationRequestService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;
    private final DocumentOwnershipGuard documentOwnershipGuard;
    private final ObjectMapper objectMapper;

    public ProformaInvoiceModificationRequestService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                                     UserPositionRepository userPositionRepository,
                                                     ApprovalRequestCommandService approvalRequestCommandService,
                                                     DocumentRevisionHistoryService documentRevisionHistoryService,
                                                     ApproverResolver approverResolver,
                                                     DocumentOwnershipGuard documentOwnershipGuard,
                                                     ObjectMapper objectMapper) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
        this.documentOwnershipGuard = documentOwnershipGuard;
        this.objectMapper = objectMapper;
    }

    public void requestModification(String piId, Long userId, ProformaInvoiceCreateRequest revisedRequest) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);

        documentOwnershipGuard.assertCanMutate(userId, proformaInvoice.getManagerId());

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!ProformaInvoiceStatus.CONFIRMED.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("확정 상태의 PI만 수정 요청할 수 있습니다.");
        }

        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.captureProformaInvoiceSnapshot(proformaInvoice);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            // STAFF: 기존 흐름 유지 — 상태 APPROVAL_PENDING 전환 + ApprovalRequest 생성.
            proformaInvoice.setStatus(ProformaInvoiceStatus.APPROVAL_PENDING);
            proformaInvoiceCommandService.save(proformaInvoice);
            Long approverId = approverResolver.resolveApproverId(userId);
            approvalRequestCommandService.save(new ApprovalRequest(
                    null,
                    ApprovalDocumentType.PI,
                    piId,
                    ApprovalRequestType.MODIFICATION,
                    userId,
                    approverId,
                    null,
                    serializeRevisedRequest(revisedRequest)
            ));
            return;
        }

        // MANAGER: 상태 변경 없이 revision history 에만 기록. 실제 필드 수정은
        // PUT /proforma-invoices/{id} (MANAGER 모드 updateDraft) 로 즉시 적용되므로
        // 결재 대기 상태를 거치지 않는다.
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                piId,
                "MANAGER_MODIFY",
                userId,
                proformaInvoice.getStatus().name(),
                "팀장이 PI 를 직접 수정했습니다.",
                beforeSnapshot
        );
    }

    private String serializeRevisedRequest(ProformaInvoiceCreateRequest revisedRequest) {
        if (revisedRequest == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(revisedRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("PI 수정 요청 payload 직렬화에 실패했습니다.", e);
        }
    }
}
