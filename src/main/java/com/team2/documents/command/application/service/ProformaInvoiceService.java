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
import com.team2.documents.command.infrastructure.client.ApproverResolver;

@Service
@Transactional
public class ProformaInvoiceService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final ApproverResolver approverResolver;
    private final DocumentAutoMailService documentAutoMailService;

    public ProformaInvoiceService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                  UserPositionRepository userPositionRepository,
                                  ApprovalRequestCommandService approvalRequestCommandService,
                                  DocumentRevisionHistoryService documentRevisionHistoryService,
                                  ApproverResolver approverResolver,
                                  DocumentAutoMailService documentAutoMailService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.approverResolver = approverResolver;
        this.documentAutoMailService = documentAutoMailService;
    }

    public void requestRegistration(String piId, Long userId) {
        requestRegistration(piId, userId, null);
    }

    public void requestRegistration(String piId, Long userId, Long approverIdOverride) {
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
            // G8: 팀장 셀프 즉시 확정 시에도 승인 경로(ApprovalRequestDocumentWorkflowService.approveDocument)
            // 와 동일하게 바이어에게 PI 자동 메일 발송. 기존엔 STAFF→MANAGER 결재 경로에서만
            // 발송되어 팀장 본인 등록 시 메일이 전혀 안 나갔음.
            documentAutoMailService.sendApprovedPiToBuyer(proformaInvoice);
            return;
        }

        Long approverId = approverIdOverride != null
                ? approverIdOverride
                : approverResolver.resolveApproverId(userId);

        proformaInvoice.setStatus(ProformaInvoiceStatus.APPROVAL_PENDING);
        proformaInvoiceCommandService.save(proformaInvoice);
        approvalRequestCommandService.save(new ApprovalRequest(
                ApprovalDocumentType.PI,
                piId,
                ApprovalRequestType.REGISTRATION,
                userId,
                approverId
        ));
    }
}
