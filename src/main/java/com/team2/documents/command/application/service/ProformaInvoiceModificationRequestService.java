package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * - MANAGER(팀장): ApprovalRequest 없이 상태만 APPROVAL_PENDING 으로 두고
 *   revision history 에 즉시 수정 요청 이벤트 기록 (이후 플로우는 PO 와 동일).
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

    public ProformaInvoiceModificationRequestService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                                     UserPositionRepository userPositionRepository,
                                                     ApprovalRequestCommandService approvalRequestCommandService,
                                                     DocumentRevisionHistoryService documentRevisionHistoryService,
                                                     ApproverResolver approverResolver,
                                                     DocumentOwnershipGuard documentOwnershipGuard) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
        this.documentOwnershipGuard = documentOwnershipGuard;
    }

    public void requestModification(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);

        documentOwnershipGuard.assertCanMutate(userId, proformaInvoice.getManagerId());

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!ProformaInvoiceStatus.CONFIRMED.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("확정 상태의 PI만 수정 요청할 수 있습니다.");
        }

        java.util.Map<String, Object> beforeSnapshot =
                documentRevisionHistoryService.captureProformaInvoiceSnapshot(proformaInvoice);
        proformaInvoice.setStatus(ProformaInvoiceStatus.APPROVAL_PENDING);
        proformaInvoiceCommandService.save(proformaInvoice);

        if (PositionLevel.STAFF.equals(positionLevel)) {
            Long approverId = approverResolver.resolveApproverId(userId);
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PI,
                    piId,
                    ApprovalRequestType.MODIFICATION,
                    userId,
                    approverId
            ));
            return;
        }

        documentRevisionHistoryService.recordProformaInvoiceEvent(
                piId,
                "REQUEST_MODIFICATION",
                userId,
                ProformaInvoiceStatus.APPROVAL_PENDING.name(),
                "관리자가 PI 수정을 요청했습니다.",
                beforeSnapshot
        );
    }
}
