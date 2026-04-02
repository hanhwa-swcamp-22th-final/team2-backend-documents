package com.team2.documents.command.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@DataJpaTest
class ProformaInvoiceRepositoryTest {

    @Autowired
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @Test
    @DisplayName("PI 엔티티를 H2에 저장하고 조회할 수 있다")
    void saveAndFindById_whenProformaInvoiceExists_thenReturnsEntity() {
        proformaInvoiceRepository.save(new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT));

        ProformaInvoice result = proformaInvoiceRepository.findByPiCode("PI2025-0001").orElseThrow();

        assertEquals("PI2025-0001", result.getPiId());
        assertEquals(ProformaInvoiceStatus.DRAFT, result.getStatus());
    }

    @Test
    @DisplayName("PI 엔티티를 수정할 수 있다")
    void update_whenProformaInvoiceStatusChanges_thenPersistsUpdatedStatus() {
        proformaInvoiceRepository.save(new ProformaInvoice("PI2025-0002", ProformaInvoiceStatus.DRAFT));

        ProformaInvoice proformaInvoice = proformaInvoiceRepository.findByPiCode("PI2025-0002").orElseThrow();
        proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
        proformaInvoiceRepository.save(proformaInvoice);

        ProformaInvoice result = proformaInvoiceRepository.findByPiCode("PI2025-0002").orElseThrow();
        assertEquals(ProformaInvoiceStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("PI 엔티티를 삭제할 수 있다")
    void delete_whenProformaInvoiceExists_thenRemovesEntity() {
        ProformaInvoice proformaInvoice = proformaInvoiceRepository.save(
                new ProformaInvoice("PI2025-0003", ProformaInvoiceStatus.DRAFT));

        proformaInvoiceRepository.delete(proformaInvoice);

        assertFalse(proformaInvoiceRepository.findByPiCode("PI2025-0003").isPresent());
    }

    @Test
    @DisplayName("PI 엔티티 전체 목록을 조회할 수 있다")
    void findAll_whenProformaInvoicesExist_thenReturnsAllEntities() {
        proformaInvoiceRepository.save(new ProformaInvoice("PI2025-0010", ProformaInvoiceStatus.DRAFT));
        proformaInvoiceRepository.save(new ProformaInvoice("PI2025-0011", ProformaInvoiceStatus.CONFIRMED));

        assertThat(proformaInvoiceRepository.findAll())
                .extracting(ProformaInvoice::getPiId, ProformaInvoice::getStatus)
                .contains(
                        org.assertj.core.groups.Tuple.tuple("PI2025-0010", ProformaInvoiceStatus.DRAFT),
                        org.assertj.core.groups.Tuple.tuple("PI2025-0011", ProformaInvoiceStatus.CONFIRMED)
                );
    }
}
