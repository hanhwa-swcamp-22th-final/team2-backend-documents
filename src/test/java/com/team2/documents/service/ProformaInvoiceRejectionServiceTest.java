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
import com.team2.documents.entity.ProformaInvoiceStatus;
import com.team2.documents.repository.ProformaInvoiceRepository;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceRejectionServiceTest {

    @Mock
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @InjectMocks
    private ProformaInvoiceRejectionService proformaInvoiceRejectionService;

    @Test
    @DisplayName("결재대기 상태의 PI를 반려하면 상태가 반려로 바뀐다")
    void reject_whenProformaInvoiceIsApprovalPending_thenChangesStatusToRejected() {
        // given
        String piId = "PI2025-0001";
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.APPROVAL_PENDING);

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));

        // when
        proformaInvoiceRejectionService.reject(piId);

        // then
        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
    }

    @Test
    @DisplayName("PI 정보가 없으면 반려 시 예외가 발생한다")
    void reject_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-9999";
        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceRejectionService.reject(piId));
    }
}
