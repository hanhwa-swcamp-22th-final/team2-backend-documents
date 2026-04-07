package com.team2.documents.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.domain.entity.DocsRevision;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.DocsRevisionRepository;

@ExtendWith(MockitoExtension.class)
class DocsSnapshotServiceTest {

    @Mock
    private DocsRevisionRepository docsRevisionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DocsSnapshotService docsSnapshotService;

    @Test
    @DisplayName("PI 스냅샷 저장 시 docs_revision에 SNAPSHOT 엔트리를 저장한다")
    void saveProformaInvoiceSnapshot_whenCalled_thenPersistsSnapshotRevision() throws Exception {
        ProformaInvoice pi = new ProformaInvoice("PI260001", ProformaInvoiceStatus.DRAFT);
        pi.setProformaInvoiceId(1L);
        pi.setTotalAmount(new BigDecimal("123.45"));
        pi.setItemsSnapshot("[{\"item\":\"Steel Coil\"}]");

        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenReturn("{\"entryType\":\"SNAPSHOT\"}");

        docsSnapshotService.saveProformaInvoiceSnapshot(pi);

        ArgumentCaptor<DocsRevision> revisionCaptor = ArgumentCaptor.forClass(DocsRevision.class);
        verify(docsRevisionRepository).save(revisionCaptor.capture());
        DocsRevision savedRevision = revisionCaptor.getValue();
        assertThat(savedRevision.getDocType()).isEqualTo("PI");
        assertThat(savedRevision.getDocId()).isEqualTo(1L);
        assertThat(savedRevision.getSnapshotData()).isEqualTo("{\"entryType\":\"SNAPSHOT\"}");
    }

    @Test
    @DisplayName("스냅샷 직렬화에 실패하면 예외를 던진다")
    void saveProformaInvoiceSnapshot_whenSerializationFails_thenThrowsException() throws Exception {
        ProformaInvoice pi = new ProformaInvoice("PI260001", ProformaInvoiceStatus.DRAFT);
        pi.setProformaInvoiceId(1L);
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new JsonProcessingException("boom") {});

        assertThrows(IllegalStateException.class,
                () -> docsSnapshotService.saveProformaInvoiceSnapshot(pi));
    }
}
