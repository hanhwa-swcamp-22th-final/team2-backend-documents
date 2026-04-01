package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;

@Service
@Transactional
public class ProformaInvoiceCommandService {

    private final ProformaInvoiceRepository proformaInvoiceRepository;

    public ProformaInvoiceCommandService(ProformaInvoiceRepository proformaInvoiceRepository) {
        this.proformaInvoiceRepository = proformaInvoiceRepository;
    }

    public ProformaInvoice findById(String piId) {
        return proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));
    }

    public ProformaInvoice save(ProformaInvoice proformaInvoice) {
        return proformaInvoiceRepository.save(proformaInvoice);
    }

    public ProformaInvoice updateStatus(String piId, ProformaInvoiceStatus status) {
        ProformaInvoice proformaInvoice = findById(piId);
        proformaInvoice.setStatus(status);
        return proformaInvoiceRepository.save(proformaInvoice);
    }
}
