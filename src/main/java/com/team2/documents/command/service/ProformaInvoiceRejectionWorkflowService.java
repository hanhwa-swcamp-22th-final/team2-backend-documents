package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;

@Service
@Transactional
public class ProformaInvoiceRejectionWorkflowService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;

    public ProformaInvoiceRejectionWorkflowService(ProformaInvoiceCommandService proformaInvoiceCommandService) {
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
