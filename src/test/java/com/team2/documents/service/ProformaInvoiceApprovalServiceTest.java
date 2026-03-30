package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.repository.ProformaInvoiceRepository;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceApprovalServiceTest {

    @Mock
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @InjectMocks
    private ProformaInvoiceApprovalService proformaInvoiceApprovalService;

    @Test
    @DisplayName("결재대기 상태의 PI를 승인하면 상태가 확정으로 바뀐다")
    void approve_whenProformaInvoiceIsApprovalPending_thenChangesStatusToConfirmed() {
        // given
        String piId = "PI2025-0001";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.APPROVAL_PENDING);

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));

        // when
        proformaInvoiceApprovalService.approve(piId);

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("PI 정보가 없으면 승인 시 예외가 발생한다")
    void approve_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-9999";
        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceApprovalService.approve(piId));
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PI를 승인하면 예외가 발생한다")
    void approve_whenProformaInvoiceIsNotApprovalPending_thenThrowsException() {
        // given
        String piId = "PI2025-0002";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.CONFIRMED);
        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> proformaInvoiceApprovalService.approve(piId));
    }
}
