package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@Service
@Transactional
public class ProformaInvoiceRejectionService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;

    public ProformaInvoiceRejectionService(ProformaInvoiceCommandService proformaInvoiceCommandService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
    }

    public void reject(String piId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.REJECTED);
        proformaInvoiceCommandService.save(proformaInvoice);
    }
}
