package com.team2.documents.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.domain.entity.DocsRevision;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.DocsRevisionRepository;

@ExtendWith(MockitoExtension.class)
class DocumentRevisionHistoryServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private DocumentJsonSupportService documentJsonSupportService;

    @Mock
    private DocsRevisionRepository docsRevisionRepository;

    @Test
    @DisplayName("PO 이벤트 기록 시 REVISION 엔트리를 저장한다")
    void recordPurchaseOrderEvent_whenCalled_thenPersistsRevisionEvent() {
        DocumentRevisionHistoryService documentRevisionHistoryService = new DocumentRevisionHistoryService(
                purchaseOrderCommandService,
                proformaInvoiceCommandService,
                documentJsonSupportService,
                docsRevisionRepository,
                new ObjectMapper()
        );
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.CONFIRMED);
        purchaseOrder.setPurchaseOrderId(1L);
        purchaseOrder.setApprovalStatus("승인");
        purchaseOrder.setTotalAmount(new BigDecimal("500.00"));

        Map<String, Object> beforeSnapshot = Map.of("status", "DRAFT");
        Map<String, Object> changes = Map.of("status", Map.of("before", "DRAFT", "after", "CONFIRMED"));

        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);
        when(documentJsonSupportService.diffSnapshots(beforeSnapshot, documentRevisionHistoryService.capturePurchaseOrderSnapshot(purchaseOrder)))
                .thenReturn(changes);

        documentRevisionHistoryService.recordPurchaseOrderEvent(
                "PO260001",
                "APPROVED",
                1L,
                "CONFIRMED",
                "승인 처리",
                beforeSnapshot
        );

        ArgumentCaptor<DocsRevision> revisionCaptor = ArgumentCaptor.forClass(DocsRevision.class);
        verify(docsRevisionRepository).save(revisionCaptor.capture());
        DocsRevision savedRevision = revisionCaptor.getValue();
        assertThat(savedRevision.getDocType()).isEqualTo("PO");
        assertThat(savedRevision.getDocId()).isEqualTo(1L);
        assertThat(savedRevision.getSnapshotData()).contains("\"entryType\":\"REVISION\"");
        assertThat(savedRevision.getSnapshotData()).contains("\"action\":\"APPROVED\"");
        assertThat(savedRevision.getSnapshotData()).contains("\"status\":\"CONFIRMED\"");
    }

    @Test
    @DisplayName("PI 스냅샷 캡처 시 주요 상태 필드를 반환한다")
    void captureProformaInvoiceSnapshot_whenCalled_thenReturnsStateMap() {
        DocumentRevisionHistoryService documentRevisionHistoryService = new DocumentRevisionHistoryService(
                purchaseOrderCommandService,
                proformaInvoiceCommandService,
                documentJsonSupportService,
                docsRevisionRepository,
                new ObjectMapper()
        );
        ProformaInvoice pi = new ProformaInvoice("PI260001", ProformaInvoiceStatus.DRAFT);
        pi.setApprovalStatus("대기");
        pi.setRequestStatus("등록요청");
        pi.setApprovalAction("등록");
        pi.setApprovalRequestedBy("이영업");
        pi.setTotalAmount(new BigDecimal("321.00"));

        Map<String, Object> snapshot = documentRevisionHistoryService.captureProformaInvoiceSnapshot(pi);

        assertThat(snapshot)
                .containsEntry("status", "DRAFT")
                .containsEntry("approvalStatus", "대기")
                .containsEntry("requestStatus", "등록요청")
                .containsEntry("approvalAction", "등록")
                .containsEntry("approvalRequestedBy", "이영업")
                .containsEntry("totalAmount", new BigDecimal("321.00"));
    }
}
