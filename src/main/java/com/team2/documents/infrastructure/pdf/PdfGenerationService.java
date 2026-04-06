package com.team2.documents.infrastructure.pdf;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ProformaInvoiceItem;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.ShipmentOrder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGenerationService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);

    public byte[] generatePurchaseOrderPdf(PurchaseOrder po, List<PurchaseOrderItem> items) {
        return generateDocument("PURCHASE ORDER", doc -> {
            addHeaderInfo(doc, "PO Code", po.getPoCode());
            addHeaderInfo(doc, "Issue Date", formatDate(po.getIssueDate()));
            addHeaderInfo(doc, "Status", po.getStatus() != null ? po.getStatus().name() : "");
            addHeaderInfo(doc, "Client", po.getClientName());
            addHeaderInfo(doc, "Client Address", po.getClientAddress());
            addHeaderInfo(doc, "Country", po.getCountry());
            addHeaderInfo(doc, "Currency", po.getCurrencyCode());
            addHeaderInfo(doc, "Incoterms", po.getIncotermsCode());
            addHeaderInfo(doc, "Named Place", po.getNamedPlace());
            addHeaderInfo(doc, "Delivery Date", formatDate(po.getDeliveryDate()));
            addHeaderInfo(doc, "Manager", po.getManagerName());
            doc.add(new Paragraph(" "));

            if (items != null && !items.isEmpty()) {
                PdfPTable table = new PdfPTable(new float[]{1f, 3f, 1.5f, 1f, 2f, 2f});
                table.setWidthPercentage(100);
                addTableHeader(table, "No", "Item Name", "Qty", "Unit", "Unit Price", "Amount");
                int idx = 1;
                for (PurchaseOrderItem item : items) {
                    addTableCell(table, String.valueOf(idx++));
                    addTableCell(table, item.getItemName());
                    addTableCell(table, String.valueOf(item.getQuantity()));
                    addTableCell(table, item.getUnit());
                    addTableCell(table, formatDecimal(item.getUnitPrice()));
                    addTableCell(table, formatDecimal(item.getAmount()));
                }
                doc.add(table);
            }

            doc.add(new Paragraph(" "));
            addHeaderInfo(doc, "Total Amount", formatDecimal(po.getTotalAmount()));
        });
    }

    public byte[] generateProformaInvoicePdf(ProformaInvoice pi, List<ProformaInvoiceItem> items) {
        return generateDocument("PROFORMA INVOICE", doc -> {
            addHeaderInfo(doc, "PI Code", pi.getPiCode());
            addHeaderInfo(doc, "Issue Date", formatDate(pi.getIssueDate()));
            addHeaderInfo(doc, "Status", pi.getStatus() != null ? pi.getStatus().name() : "");
            addHeaderInfo(doc, "Client", pi.getClientName());
            addHeaderInfo(doc, "Client Address", pi.getClientAddress());
            addHeaderInfo(doc, "Country", pi.getCountry());
            addHeaderInfo(doc, "Currency", pi.getCurrencyCode());
            addHeaderInfo(doc, "Incoterms", pi.getIncotermsCode());
            addHeaderInfo(doc, "Named Place", pi.getNamedPlace());
            addHeaderInfo(doc, "Delivery Date", formatDate(pi.getDeliveryDate()));
            addHeaderInfo(doc, "Manager", pi.getManagerName());
            doc.add(new Paragraph(" "));

            if (items != null && !items.isEmpty()) {
                PdfPTable table = new PdfPTable(new float[]{1f, 3f, 1.5f, 1f, 2f, 2f});
                table.setWidthPercentage(100);
                addTableHeader(table, "No", "Item Name", "Qty", "Unit", "Unit Price", "Amount");
                int idx = 1;
                for (ProformaInvoiceItem item : items) {
                    addTableCell(table, String.valueOf(idx++));
                    addTableCell(table, item.getItemName());
                    addTableCell(table, String.valueOf(item.getQuantity()));
                    addTableCell(table, item.getUnit());
                    addTableCell(table, formatDecimal(item.getUnitPrice()));
                    addTableCell(table, formatDecimal(item.getAmount()));
                }
                doc.add(table);
            }

            doc.add(new Paragraph(" "));
            addHeaderInfo(doc, "Total Amount", formatDecimal(pi.getTotalAmount()));
        });
    }

    public byte[] generateCommercialInvoicePdf(CommercialInvoice ci) {
        return generateDocument("COMMERCIAL INVOICE", doc -> {
            addHeaderInfo(doc, "CI Code", ci.getCiCode());
            addHeaderInfo(doc, "Invoice Date", formatDate(ci.getInvoiceDate()));
            addHeaderInfo(doc, "Status", ci.getStatus());
            addHeaderInfo(doc, "Total Amount", formatDecimal(ci.getTotalAmount()));
        });
    }

    public byte[] generatePackingListPdf(PackingList pl) {
        return generateDocument("PACKING LIST", doc -> {
            addHeaderInfo(doc, "PL Code", pl.getPlCode());
            addHeaderInfo(doc, "Invoice Date", formatDate(pl.getInvoiceDate()));
            addHeaderInfo(doc, "Status", pl.getStatus());
            addHeaderInfo(doc, "Gross Weight", formatDecimal(pl.getGrossWeight()));
        });
    }

    public byte[] generateShipmentOrderPdf(ShipmentOrder so) {
        return generateDocument("SHIPMENT ORDER", doc -> {
            addHeaderInfo(doc, "Shipment Order Code", so.getShipmentOrderCode());
            addHeaderInfo(doc, "Issue Date", formatDate(so.getIssueDate()));
            addHeaderInfo(doc, "Status", so.getStatus());
            addHeaderInfo(doc, "Client", so.getClientName());
            addHeaderInfo(doc, "Country", so.getCountry());
            addHeaderInfo(doc, "Due Date", formatDate(so.getDueDate()));
            addHeaderInfo(doc, "Manager", so.getManagerName());
            addHeaderInfo(doc, "Item", so.getItemName());
        });
    }

    public byte[] generateProductionOrderPdf(ProductionOrder po) {
        return generateDocument("PRODUCTION ORDER", doc -> {
            addHeaderInfo(doc, "Production Order Code", po.getProductionOrderId());
            addHeaderInfo(doc, "Order Date", formatDate(po.getOrderDate()));
            addHeaderInfo(doc, "Status", po.getStatus());
            addHeaderInfo(doc, "Client", po.getClientName());
            addHeaderInfo(doc, "Country", po.getCountry());
            addHeaderInfo(doc, "Due Date", formatDate(po.getDueDate()));
            addHeaderInfo(doc, "Manager", po.getManagerName());
            addHeaderInfo(doc, "Item", po.getItemName());
        });
    }

    private byte[] generateDocument(String title, DocumentContentWriter writer) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20);
            document.add(titleParagraph);

            writer.write(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for {}", title, e);
            throw new RuntimeException("Failed to generate PDF: " + title, e);
        }
    }

    private void addHeaderInfo(Document doc, String label, String value) {
        try {
            if (value == null || value.isBlank()) {
                return;
            }
            Paragraph p = new Paragraph();
            p.add(new Phrase(label + ": ", LABEL_FONT));
            p.add(new Phrase(value, VALUE_FONT));
            p.setSpacingAfter(4);
            doc.add(p);
        } catch (Exception e) {
            log.warn("Failed to add header info: {} = {}", label, value, e);
        }
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            cell.setBackgroundColor(new java.awt.Color(220, 220, 220));
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", BODY_FONT));
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "";
    }

    private String formatDecimal(BigDecimal value) {
        return value != null ? value.toPlainString() : "";
    }

    @FunctionalInterface
    private interface DocumentContentWriter {
        void write(Document document) throws Exception;
    }
}
