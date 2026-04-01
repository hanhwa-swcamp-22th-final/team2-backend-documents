package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@Service
@Transactional
public class ProformaInvoiceRejectionWorkflowService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;

    public ProformaInvoiceRejectionWorkflowService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                                   DocumentRevisionHistoryService documentRevisionHistoryService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
    }

    public void reject(String piId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        java.util.Map<String, Object> beforeSnapshot = documentRevisionHistoryService.captureProformaInvoiceSnapshot(proformaInvoice);
        proformaInvoice.setStatus(ProformaInvoiceStatus.REJECTED);
        proformaInvoiceCommandService.save(proformaInvoice);
        documentRevisionHistoryService.recordProformaInvoiceEvent(
                piId,
                "REJECTED",
                proformaInvoice.getManagerId(),
                ProformaInvoiceStatus.REJECTED.name(),
                "PI 등록 요청이 반려되었습니다.",
                beforeSnapshot
        );
    }
}
