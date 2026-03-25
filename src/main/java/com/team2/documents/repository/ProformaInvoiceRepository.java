package com.team2.documents.repository;

import java.util.Optional;

import com.team2.documents.entity.ProformaInvoice;

public interface ProformaInvoiceRepository {

    Optional<ProformaInvoice> findById(String piId);
}
