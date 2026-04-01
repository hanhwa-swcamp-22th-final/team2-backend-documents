package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@Service
@Transactional
public class ProformaInvoiceApprovalService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;

    public ProformaInvoiceApprovalService(ProformaInvoiceCommandService proformaInvoiceCommandService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
    }

    public void approve(String piId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 승인할 수 있습니다.");
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
        proformaInvoiceCommandService.save(proformaInvoice);
    }
}
