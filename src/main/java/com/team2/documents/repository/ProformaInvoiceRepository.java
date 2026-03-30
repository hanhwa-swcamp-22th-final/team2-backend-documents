package com.team2.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team2.documents.entity.ProformaInvoice;

public interface ProformaInvoiceRepository extends JpaRepository<ProformaInvoice, String> {
}
