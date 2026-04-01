package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceCommandServiceTest {

    @Mock
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @InjectMocks
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Test
    @DisplayName("PI ID로 조회 시 해당 PI를 반환한다")
    void findById_whenProformaInvoiceExists_thenReturnsProformaInvoice() {
        // given
        ProformaInvoice pi = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT);
        when(proformaInvoiceRepository.findById("PI2025-0001")).thenReturn(Optional.of(pi));

        // when
        ProformaInvoice result = proformaInvoiceCommandService.findById("PI2025-0001");

        // then
        assertEquals("PI2025-0001", result.getPiId());
    }

    @Test
    @DisplayName("존재하지 않는 PI ID로 조회 시 예외가 발생한다")
    void findById_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        when(proformaInvoiceRepository.findById("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceCommandService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("PI 저장 시 저장된 PI를 반환한다")
    void save_whenProformaInvoiceIsValid_thenReturnsSavedProformaInvoice() {
        // given
        ProformaInvoice pi = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT);
        when(proformaInvoiceRepository.save(any(ProformaInvoice.class))).thenReturn(pi);

        // when
        ProformaInvoice result = proformaInvoiceCommandService.save(pi);

        // then
        assertEquals("PI2025-0001", result.getPiId());
    }

    @Test
    @DisplayName("PI 상태 변경 시 상태가 변경된 PI를 반환한다")
    void updateStatus_whenProformaInvoiceExists_thenChangesStatus() {
        // given
        ProformaInvoice pi = new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT);
        when(proformaInvoiceRepository.findById("PI2025-0001")).thenReturn(Optional.of(pi));
        when(proformaInvoiceRepository.save(any(ProformaInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ProformaInvoice result = proformaInvoiceCommandService.updateStatus("PI2025-0001", ProformaInvoiceStatus.CONFIRMED);

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 PI 상태 변경 시 예외가 발생한다")
    void updateStatus_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        when(proformaInvoiceRepository.findById("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceCommandService.updateStatus("NOT-EXIST", ProformaInvoiceStatus.CONFIRMED));
    }
}
