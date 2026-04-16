package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.CommercialInvoice;

public interface CommercialInvoiceJpaRepository extends JpaRepository<CommercialInvoice, Long> {

    Optional<CommercialInvoice> findByCiCode(String ciCode);

    Optional<CommercialInvoice> findByPoId(Long poId);

    long countByPoId(Long poId);
}
