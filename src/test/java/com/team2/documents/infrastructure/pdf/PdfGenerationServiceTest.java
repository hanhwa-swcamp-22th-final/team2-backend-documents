package com.team2.documents.infrastructure.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ProformaInvoiceItem;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;

class PdfGenerationServiceTest {

    private final PdfGenerationService pdfGenerationService = new PdfGenerationService();

    @Test
    @DisplayName("구매발주서 PDF 생성 시 문서 제목과 품목 정보가 포함된다")
    void generatePurchaseOrderPdf_containsTitleAndItemDetails() throws Exception {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.CONFIRMED);
        purchaseOrder.setIssueDate(LocalDate.of(2026, 4, 7));
        purchaseOrder.setClientName("ACME Trading");
        purchaseOrder.setClientAddress("Seoul, Korea");
        purchaseOrder.setCountry("KOREA");
        purchaseOrder.setCurrencyCode("USD");
        purchaseOrder.setIncotermsCode("FOB");
        purchaseOrder.setNamedPlace("Busan Port");
        purchaseOrder.setDeliveryDate(LocalDate.of(2026, 4, 30));
        purchaseOrder.setManagerName("Kim Manager");
        purchaseOrder.setTotalAmount(new BigDecimal("1500.00"));

        List<PurchaseOrderItem> items = List.of(
                new PurchaseOrderItem(1, "Steel Coil", 10, "EA", new BigDecimal("100.00"), new BigDecimal("1000.00"), "priority"),
                new PurchaseOrderItem(2, "Copper Wire", 5, "BOX", new BigDecimal("100.00"), new BigDecimal("500.00"), "fragile")
        );

        byte[] pdfBytes = pdfGenerationService.generatePurchaseOrderPdf(purchaseOrder, items);

        assertThat(pdfBytes).isNotEmpty();
        assertThat(new String(pdfBytes, 0, 4)).isEqualTo("%PDF");

        String extractedText = extractText(pdfBytes);
        assertThat(extractedText).contains("PURCHASE ORDER");
        assertThat(extractedText).contains("PO Code: PO260001");
        assertThat(extractedText).contains("Client: ACME Trading");
        assertThat(extractedText).contains("Steel Coil");
        assertThat(extractedText).contains("Copper Wire");
        assertThat(extractedText).contains("Total Amount: 1500.00");
    }

    @Test
    @DisplayName("견적송장 PDF 생성 시 문서 제목과 총액 정보가 포함된다")
    void generateProformaInvoicePdf_containsTitleAndTotalAmount() throws Exception {
        ProformaInvoice proformaInvoice = new ProformaInvoice("PI260001", ProformaInvoiceStatus.CONFIRMED);
        proformaInvoice.setIssueDate(LocalDate.of(2026, 4, 7));
        proformaInvoice.setClientName("Global Buyer");
        proformaInvoice.setClientAddress("London, UK");
        proformaInvoice.setCountry("UNITED KINGDOM");
        proformaInvoice.setCurrencyCode("GBP");
        proformaInvoice.setIncotermsCode("CIF");
        proformaInvoice.setNamedPlace("London");
        proformaInvoice.setDeliveryDate(LocalDate.of(2026, 5, 10));
        proformaInvoice.setManagerName("Lee Sales");
        proformaInvoice.setTotalAmount(new BigDecimal("725.30"));

        List<ProformaInvoiceItem> items = List.of(
                new ProformaInvoiceItem(1, "Aluminum Ingot", 3, "EA", new BigDecimal("200.10"), new BigDecimal("600.30"), "sample"),
                new ProformaInvoiceItem(2, "Polyethylene Pellet", 1, "BAG", new BigDecimal("125.00"), new BigDecimal("125.00"), "sample")
        );

        byte[] pdfBytes = pdfGenerationService.generateProformaInvoicePdf(proformaInvoice, items);

        assertThat(pdfBytes).isNotEmpty();

        String extractedText = extractText(pdfBytes);
        assertThat(extractedText).contains("PROFORMA INVOICE");
        assertThat(extractedText).contains("PI Code: PI260001");
        assertThat(extractedText).contains("Client: Global Buyer");
        assertThat(extractedText).contains("Aluminum Ingot");
        assertThat(extractedText).contains("Polyethylene Pellet");
        assertThat(extractedText).contains("Total Amount: 725.30");
    }

    @Test
    @DisplayName("출하지시서 PDF 생성 시 핵심 헤더 정보가 포함된다")
    void generateShipmentOrderPdf_containsCoreHeaderFields() throws Exception {
        ShipmentOrder shipmentOrder = new ShipmentOrder();
        shipmentOrder.setShipmentOrderCode("SO260001");
        shipmentOrder.setPoCode("PO260001");
        shipmentOrder.setStatus("READY");
        shipmentOrder.setLinkedDocuments("{\"po\":\"PO260001\"}");
        ReflectionTestUtils.setField(shipmentOrder, "issueDate", LocalDate.of(2026, 4, 7));
        ReflectionTestUtils.setField(shipmentOrder, "dueDate", LocalDate.of(2026, 4, 20));
        ReflectionTestUtils.setField(shipmentOrder, "clientName", "Pacific Imports");
        ReflectionTestUtils.setField(shipmentOrder, "country", "JAPAN");
        ReflectionTestUtils.setField(shipmentOrder, "managerName", "Park Shipping");
        ReflectionTestUtils.setField(shipmentOrder, "itemName", "Steel Coil");

        byte[] pdfBytes = pdfGenerationService.generateShipmentOrderPdf(shipmentOrder);

        assertThat(pdfBytes).isNotEmpty();

        String extractedText = extractText(pdfBytes);
        assertThat(extractedText).contains("SHIPMENT ORDER");
        assertThat(extractedText).contains("Shipment Order Code: SO260001");
        assertThat(extractedText).contains("Status: READY");
        assertThat(extractedText).contains("Client: Pacific Imports");
        assertThat(extractedText).contains("Country: JAPAN");
        assertThat(extractedText).contains("Manager: Park Shipping");
        assertThat(extractedText).contains("Item: Steel Coil");
    }

    private String extractText(byte[] pdfBytes) throws Exception {
        try (PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(pdfBytes))) {
            PdfTextExtractor textExtractor = new PdfTextExtractor(pdfReader);
            StringBuilder fullText = new StringBuilder();
            for (int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
                fullText.append(textExtractor.getTextFromPage(page));
            }
            return fullText.toString();
        }
    }
}
