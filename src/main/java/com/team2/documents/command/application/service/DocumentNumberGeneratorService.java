package com.team2.documents.command.application.service;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.DocumentNumberSequence;
import com.team2.documents.command.domain.repository.DocumentNumberSequenceRepository;

@Service
@Transactional
public class DocumentNumberGeneratorService {

    private final DocumentNumberSequenceRepository documentNumberSequenceRepository;

    public DocumentNumberGeneratorService(DocumentNumberSequenceRepository documentNumberSequenceRepository) {
        this.documentNumberSequenceRepository = documentNumberSequenceRepository;
    }

    public String nextPurchaseOrderId() {
        return next("PO");
    }

    public String nextProformaInvoiceId() {
        return next("PI");
    }

    public String nextCommercialInvoiceId() {
        return next("CI");
    }

    public String nextPackingListId() {
        return next("PL");
    }

    public String nextShipmentOrderId() {
        return next("SO");
    }

    public String nextProductionOrderId() {
        return next("MO");
    }

    private String next(String documentType) {
        String prefix = documentType + String.format("%02d", LocalDate.now().getYear() % 100);
        DocumentNumberSequence sequence = lockOrCreate(prefix);
        sequence.increment();
        documentNumberSequenceRepository.save(sequence);
        return prefix + String.format("%04d", sequence.getLastNumber());
    }

    private DocumentNumberSequence lockOrCreate(String prefix) {
        return documentNumberSequenceRepository.findByPrefix(prefix)
                .orElseGet(() -> createAndLock(prefix));
    }

    private DocumentNumberSequence createAndLock(String prefix) {
        try {
            return documentNumberSequenceRepository.save(new DocumentNumberSequence(prefix, 0L));
        } catch (DataIntegrityViolationException exception) {
            return documentNumberSequenceRepository.findByPrefix(prefix)
                    .orElseThrow(() -> exception);
        }
    }
}
