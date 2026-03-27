package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.repository.ProformaInvoiceRepository;

@Service
public class ProformaInvoiceApprovalService {

    private final ProformaInvoiceRepository proformaInvoiceRepository;

    public ProformaInvoiceApprovalService(ProformaInvoiceRepository proformaInvoiceRepository) {
        this.proformaInvoiceRepository = proformaInvoiceRepository;
    }

    public void approve(String piId) {
        proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."))
                .approve();
    }
}
