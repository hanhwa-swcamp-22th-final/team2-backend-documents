package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestDocumentWorkflowServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    @Mock
    private DocumentAutoMailService documentAutoMailService;

    @InjectMocks
    private ApprovalRequestDocumentWorkflowService approvalRequestDocumentWorkflowService;

    @Test
    @DisplayName("PO 승인 시 CONFIRMED로 변경하고 후속 문서를 생성한다")
    void approveDocument_whenPurchaseOrderPending_thenConfirmsAndGeneratesDocuments() {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.APPROVAL_PENDING);
        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);

        approvalRequestDocumentWorkflowService.approveDocument(ApprovalDocumentType.PO, "PO260001");

        assertEquals(PurchaseOrderStatus.CONFIRMED, purchaseOrder.getStatus());
        verify(purchaseOrderCommandService).save(purchaseOrder);
        verify(purchaseOrderDocumentGenerationService).generateOnConfirmation("PO260001");
    }

    @Test
    @DisplayName("PI 승인 시 바이어에게 자동 메일을 발송한다")
    void approveDocument_whenProformaInvoicePending_thenSendsAutoMail() {
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI260001", ProformaInvoiceStatus.APPROVAL_PENDING);
        when(proformaInvoiceCommandService.findById("PI260001")).thenReturn(proformaInvoice);

        approvalRequestDocumentWorkflowService.approveDocument(ApprovalDocumentType.PI, "PI260001");

        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
        verify(proformaInvoiceCommandService).save(proformaInvoice);
        verify(documentAutoMailService).sendApprovedPiToBuyer(proformaInvoice);
    }

    @Test
    @DisplayName("PI 반려 시 REJECTED로 변경한다")
    void rejectDocument_whenProformaInvoicePending_thenRejects() {
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI260001", ProformaInvoiceStatus.APPROVAL_PENDING);
        when(proformaInvoiceCommandService.findById("PI260001")).thenReturn(proformaInvoice);

        approvalRequestDocumentWorkflowService.rejectDocument(ApprovalDocumentType.PI, "PI260001");

        assertEquals(ProformaInvoiceStatus.REJECTED, proformaInvoice.getStatus());
        verify(proformaInvoiceCommandService).save(proformaInvoice);
    }

    @Test
    @DisplayName("결재대기 상태가 아닌 PO는 승인할 수 없다")
    void approveDocument_whenPurchaseOrderNotPending_thenThrowsException() {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderCommandService.findById("PO260001")).thenReturn(purchaseOrder);

        assertThrows(IllegalStateException.class,
                () -> approvalRequestDocumentWorkflowService.approveDocument(ApprovalDocumentType.PO, "PO260001"));
    }
}
