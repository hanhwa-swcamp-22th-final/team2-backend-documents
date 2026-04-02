package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceItemCreateRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceCreationServiceTest {

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private DocumentNumberGeneratorService documentNumberGeneratorService;

    @Mock
    private DocsSnapshotService docsSnapshotService;

    @Mock
    private DocumentRevisionHistoryService documentRevisionHistoryService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProformaInvoiceCreationService proformaInvoiceCreationService;

    @Test
    @DisplayName("PI 생성 시 스냅샷과 docs_revision 이력이 저장된다")
    void createProformaInvoice_whenRequestContainsItems_thenStoresConsistentMetadata() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"itemName\":\"Bolt\"}]");
        when(proformaInvoiceCommandService.save(any(ProformaInvoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProformaInvoiceCreateRequest request = new ProformaInvoiceCreateRequest(
                "PI260001",
                LocalDate.of(2026, 4, 1),
                10,
                1,
                2L,
                LocalDate.of(2026, 4, 20),
                "FOB",
                "Busan",
                null,
                "ABC Trading",
                "Seoul",
                "KR",
                "USD",
                "Kim",
                2L,
                List.of(new ProformaInvoiceItemCreateRequest(
                        100,
                        "Bolt",
                        5,
                        "EA",
                        new BigDecimal("10.00"),
                        null,
                        "urgent"
                ))
        );

        ProformaInvoice created = proformaInvoiceCreationService.create(request);

        assertEquals(ProformaInvoiceStatus.DRAFT, created.getStatus());
        assertEquals(new BigDecimal("50.00"), created.getTotalAmount());
        assertEquals("[{\"itemName\":\"Bolt\"}]", created.getItemsSnapshot());
        assertEquals("[]", created.getLinkedDocuments());
        assertTrue(created.getApprovalRequestedAt() == null);
        verify(docsSnapshotService).saveProformaInvoiceSnapshot(created);
        verify(documentRevisionHistoryService).recordProformaInvoiceEvent(
                "PI260001",
                "CREATE",
                2L,
                ProformaInvoiceStatus.DRAFT.name(),
                "PI 초안을 생성했습니다."
        );
    }
}
