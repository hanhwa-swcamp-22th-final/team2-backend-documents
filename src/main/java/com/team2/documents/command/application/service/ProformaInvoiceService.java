package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@Service
@Transactional
public class ProformaInvoiceService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public ProformaInvoiceService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                  UserPositionRepository userPositionRepository,
                                  ApprovalRequestCommandService approvalRequestCommandService,
                                  DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void requestRegistration(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!ProformaInvoiceStatus.DRAFT.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("초안 상태의 PI만 등록 요청할 수 있습니다.");
        }

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
            proformaInvoiceCommandService.save(proformaInvoice);
            documentRevisionHistoryService.recordProformaInvoiceEvent(
                    piId,
                    "REQUEST_REGISTRATION",
                    userId,
                    ProformaInvoiceStatus.CONFIRMED.name(),
                    "관리자가 PI를 즉시 확정했습니다."
            );
            return;
        }

        proformaInvoice.setStatus(ProformaInvoiceStatus.APPROVAL_PENDING);
        proformaInvoiceCommandService.save(proformaInvoice);
        approvalRequestCommandService.save(new ApprovalRequest(
                ApprovalDocumentType.PI,
                piId,
                ApprovalRequestType.REGISTRATION,
                userId,
                1L
        ));
    }
}
