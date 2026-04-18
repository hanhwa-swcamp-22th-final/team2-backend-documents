package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

/**
 * 결재대기 상태의 PI 에 대해 요청자 본인이 결재 요청을 취소한다.
 * - REGISTRATION 취소: 상태를 DRAFT 로 복구 (초안으로 돌아감)
 * - MODIFICATION / DELETION 취소: 상태를 CONFIRMED 로 복구 (원본 확정 문서 상태 유지)
 * ApprovalRequest 엔티티는 삭제돼 향후 PENDING 조회에서 제외된다.
 */
@Service
@Transactional
public class ProformaInvoiceApprovalCancellationService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public ProformaInvoiceApprovalCancellationService(
            ProformaInvoiceCommandService proformaInvoiceCommandService,
            ApprovalRequestCommandService approvalRequestCommandService,
            DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void cancelApprovalRequest(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 요청을 취소할 수 있습니다.");
        }

        ApprovalRequest request = approvalRequestCommandService.findPendingByDocument(
                ApprovalDocumentType.PI, piId);
        if (request.getRequesterId() == null || !request.getRequesterId().equals(userId)) {
            throw new IllegalStateException("본인이 요청한 결재만 취소할 수 있습니다.");
        }

        ProformaInvoiceStatus target = ApprovalRequestType.REGISTRATION.equals(request.getRequestType())
                ? ProformaInvoiceStatus.DRAFT
                : ProformaInvoiceStatus.CONFIRMED;
        proformaInvoice.setStatus(target);
        proformaInvoiceCommandService.save(proformaInvoice);

        approvalRequestCommandService.cancelPendingByDocument(ApprovalDocumentType.PI, piId);

        documentRevisionHistoryService.recordProformaInvoiceEvent(
                piId,
                "APPROVAL_CANCELLED",
                userId,
                target.name(),
                "요청자가 결재 요청을 취소하여 상태가 " + target.name() + " 로 복구되었습니다."
        );
    }
}
