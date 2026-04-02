package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team2.documents.command.domain.entity.ProformaInvoice;

public interface ProformaInvoiceRepository extends JpaRepository<ProformaInvoice, Long> {

    Optional<ProformaInvoice> findByPiCode(String piCode);
}
