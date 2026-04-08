package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.infrastructure.pdf.PdfGenerationService;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentAutoMailService {

    private final AutoEmailRecipientResolver autoEmailRecipientResolver;
    private final PdfGenerationService pdfGenerationService;
    private final JavaMailSender javaMailSender;

    @Value("${MAIL_USERNAME:}")
    private String mailUsername;

    public DocumentAutoMailService(AutoEmailRecipientResolver autoEmailRecipientResolver,
                                   PdfGenerationService pdfGenerationService,
                                   JavaMailSender javaMailSender) {
        this.autoEmailRecipientResolver = autoEmailRecipientResolver;
        this.pdfGenerationService = pdfGenerationService;
        this.javaMailSender = javaMailSender;
    }

    public void sendApprovedPiToBuyer(ProformaInvoice proformaInvoice) {
        List<String> recipients = autoEmailRecipientResolver.findBuyerEmailsByClientId(proformaInvoice.getClientId());
        if (recipients.isEmpty()) {
            log.warn("PI 자동 메일 수신자(바이어)를 찾지 못했습니다. piId={}", proformaInvoice.getPiId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateProformaInvoicePdf(proformaInvoice, proformaInvoice.getItems());
        String recipientName = autoEmailRecipientResolver.findPrimaryBuyerNameByClientId(proformaInvoice.getClientId());
        sendEmail(
                recipients,
                "[자동발송] PI 승인 완료 - " + proformaInvoice.getPiId(),
                buildBuyerBody(recipientName, proformaInvoice.getPiId()),
                "PI.pdf",
                pdf
        );
    }

    public void sendShipmentOrderToShippingTeam(PurchaseOrder purchaseOrder, ShipmentOrder shipmentOrder) {
        List<String> recipients = autoEmailRecipientResolver.findShippingTeamEmails();
        if (recipients.isEmpty()) {
            log.warn("출하팀 자동 메일 수신자를 찾지 못했습니다. shipmentOrderId={}", shipmentOrder.getShipmentOrderId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateShipmentOrderPdf(shipmentOrder);
        sendEmail(
                recipients,
                "[자동발송] 출하지시서 생성 완료 - " + shipmentOrder.getShipmentOrderId(),
                buildTeamBody("출하팀", purchaseOrder.getPoId(), shipmentOrder.getShipmentOrderId()),
                "SHIPPING_ORDER.pdf",
                pdf
        );
    }

    public void sendProductionOrderToProductionTeam(PurchaseOrder purchaseOrder, ProductionOrder productionOrder) {
        List<String> recipients = autoEmailRecipientResolver.findProductionTeamEmails();
        if (recipients.isEmpty()) {
            log.warn("생산팀 자동 메일 수신자를 찾지 못했습니다. productionOrderId={}", productionOrder.getProductionOrderId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateProductionOrderPdf(productionOrder);
        sendEmail(
                recipients,
                "[자동발송] 생산지시서 생성 완료 - " + productionOrder.getProductionOrderId(),
                buildTeamBody("생산팀", purchaseOrder.getPoId(), productionOrder.getProductionOrderId()),
                "PRODUCTION_ORDER.pdf",
                pdf
        );
    }

    private void sendEmail(List<String> recipients,
                           String subject,
                           String body,
                           String attachmentFilename,
                           byte[] attachmentData) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (mailUsername != null && !mailUsername.isBlank()) {
                helper.setFrom(mailUsername);
            }
            helper.setTo(recipients.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(body);
            DataSource dataSource = new ByteArrayDataSource(attachmentData, "application/pdf");
            helper.addAttachment(attachmentFilename, dataSource);
            javaMailSender.send(message);
            log.info("자동 메일 발송 완료. recipients={}, subject={}", recipients, subject);
        } catch (Exception e) {
            log.warn("자동 메일 발송에 실패했습니다. subject={}, recipients={}", subject, recipients, e);
        }
    }

    private String buildBuyerBody(String recipientName, String piId) {
        return "Dear " + recipientName + ",\n\n"
                + "The approved PI document is attached.\n"
                + "PI No: " + piId + "\n\n"
                + "Best regards";
    }

    private String buildTeamBody(String teamName, String poId, String documentId) {
        return teamName + " 확인용 자동 발송 메일입니다.\n\n"
                + "PO No: " + poId + "\n"
                + "Document No: " + documentId + "\n\n"
                + "확인 부탁드립니다.";
    }
}
