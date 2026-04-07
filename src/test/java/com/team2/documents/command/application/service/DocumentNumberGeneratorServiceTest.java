package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.DocumentNumberSequence;
import com.team2.documents.command.domain.repository.DocumentNumberSequenceRepository;

@ExtendWith(MockitoExtension.class)
class DocumentNumberGeneratorServiceTest {

    @Mock
    private DocumentNumberSequenceRepository documentNumberSequenceRepository;

    @InjectMocks
    private DocumentNumberGeneratorService documentNumberGeneratorService;

    @Test
    @DisplayName("기존 시퀀스가 있으면 다음 PO 번호를 생성한다")
    void nextPurchaseOrderId_whenSequenceExists_thenReturnsIncrementedId() {
        String prefix = "PO" + String.format("%02d", LocalDate.now().getYear() % 100);
        DocumentNumberSequence sequence = new DocumentNumberSequence(prefix, 7L);
        when(documentNumberSequenceRepository.findByPrefix(prefix)).thenReturn(Optional.of(sequence));

        String result = documentNumberGeneratorService.nextPurchaseOrderId();

        assertEquals(prefix + "0008", result);
        verify(documentNumberSequenceRepository).save(sequence);
    }

    @Test
    @DisplayName("시퀀스가 없으면 새로 생성한 뒤 다음 PI 번호를 생성한다")
    void nextProformaInvoiceId_whenSequenceDoesNotExist_thenCreatesAndReturnsFirstId() {
        String prefix = "PI" + String.format("%02d", LocalDate.now().getYear() % 100);
        when(documentNumberSequenceRepository.findByPrefix(prefix)).thenReturn(Optional.empty());
        when(documentNumberSequenceRepository.save(any(DocumentNumberSequence.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = documentNumberGeneratorService.nextProformaInvoiceId();

        assertEquals(prefix + "0001", result);
        verify(documentNumberSequenceRepository, times(2)).save(any(DocumentNumberSequence.class));
    }
}
