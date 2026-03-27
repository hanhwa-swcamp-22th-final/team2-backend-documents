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
        proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."))
                .reject();
    }
}
