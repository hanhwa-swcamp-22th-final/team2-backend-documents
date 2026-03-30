package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.repository.ProformaInvoiceRepository;

@Service
public class ProformaInvoiceRejectionService {

    private final ProformaInvoiceRepository proformaInvoiceRepository;

    public ProformaInvoiceRejectionService(ProformaInvoiceRepository proformaInvoiceRepository) {
        this.proformaInvoiceRepository = proformaInvoiceRepository;
    }

    public void reject(String piId) {
        com.team2.documents.entity.ProformaInvoice proformaInvoice = proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));
        if (!com.team2.documents.entity.enums.ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        proformaInvoice.setStatus(com.team2.documents.entity.enums.ProformaInvoiceStatus.REJECTED);
    }
}
