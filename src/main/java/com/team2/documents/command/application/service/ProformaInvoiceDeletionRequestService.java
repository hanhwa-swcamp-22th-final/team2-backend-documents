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

@Service
@Transactional
public class ProformaInvoiceDeletionRequestService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;

    public ProformaInvoiceDeletionRequestService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                                 UserPositionRepository userPositionRepository,
                                                 ApprovalRequestCommandService approvalRequestCommandService,
                                                 DocumentRevisionHistoryService documentRevisionHistoryService,
                                                 ApproverResolver approverResolver) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
    }

    public void requestDeletion(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!ProformaInvoiceStatus.CONFIRMED.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("확정 상태의 PI만 삭제 요청할 수 있습니다.");
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
                    ApprovalRequestType.DELETION,
                    userId,
                    approverId
            ));
            return;
        }

        // MANAGER: immediate soft delete
        proformaInvoice.setStatus(ProformaInvoiceStatus.DELETED);
        proformaInvoiceCommandService.save(proformaInvoice);

        documentRevisionHistoryService.recordProformaInvoiceEvent(
                piId,
                "DELETION_COMPLETED",
                userId,
                ProformaInvoiceStatus.DELETED.name(),
                "관리자가 PI를 즉시 삭제 처리했습니다.",
                beforeSnapshot
        );
    }
}
