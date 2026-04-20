package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team2.documents.command.application.dto.EmailLogInternalRequest;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.infrastructure.client.ActivityFeignClient;
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
    private final ActivityFeignClient activityFeignClient;

    @Value("${MAIL_USERNAME:}")
    private String mailUsername;

    public DocumentAutoMailService(AutoEmailRecipientResolver autoEmailRecipientResolver,
                                   PdfGenerationService pdfGenerationService,
                                   JavaMailSender javaMailSender,
                                   ActivityFeignClient activityFeignClient) {
        this.autoEmailRecipientResolver = autoEmailRecipientResolver;
        this.pdfGenerationService = pdfGenerationService;
        this.javaMailSender = javaMailSender;
        this.activityFeignClient = activityFeignClient;
    }

    public void sendApprovedPiToBuyer(ProformaInvoice proformaInvoice) {
        List<String> recipients = autoEmailRecipientResolver.findBuyerEmailsByClientId(proformaInvoice.getClientId());
        if (recipients.isEmpty()) {
            log.warn("PI 자동 메일 수신자(바이어)를 찾지 못했습니다. piId={}", proformaInvoice.getPiId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateProformaInvoicePdf(proformaInvoice, proformaInvoice.getItems());
        String recipientName = autoEmailRecipientResolver.findPrimaryBuyerNameByClientId(proformaInvoice.getClientId());
        // 외국 거래처 대상이므로 제목은 영문으로 통일 (Issue F).
        String subject = "Proforma Invoice " + proformaInvoice.getPiId() + " — Approved";
        boolean sent = sendEmail(
                recipients,
                subject,
                buildBuyerBody(recipientName, proformaInvoice.getPiId()),
                "PI.pdf",
                pdf
        );
        // 발송 후 Activity email-logs 에 기록. 실패해도 문서 흐름은 계속 (best-effort).
        logEmailToActivity(
                proformaInvoice.getClientId(),
                null,
                subject,
                recipientName,
                recipients,
                proformaInvoice.getManagerId(),
                sent ? "SENT" : "FAILED",
                List.of("PI"),
                List.of("PI.pdf")
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

    private boolean sendEmail(List<String> recipients,
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
            return true;
        } catch (Exception e) {
            log.warn("자동 메일 발송에 실패했습니다. subject={}, recipients={}", subject, recipients, e);
            return false;
        }
    }

    /**
     * Activity 서비스의 email_logs 테이블에 자동 메일 발송 이력을 기록.
     * 이전엔 자동 메일이 이력 저장 경로를 거치지 않아 email 이력 페이지에서 PI 승인
     * 자동 발송이 누락돼 있었음 (Issue B). 첫 수신자를 recipient 로 기록.
     * Feign 실패는 warn 만 찍고 삼킴 — 문서 흐름을 끊지 않는다.
     */
    private void logEmailToActivity(Integer clientId,
                                    String poId,
                                    String subject,
                                    String recipientName,
                                    List<String> recipients,
                                    Long senderUserId,
                                    String status,
                                    List<String> docTypes,
                                    List<String> attachmentFilenames) {
        try {
            String firstRecipient = recipients == null || recipients.isEmpty() ? null : recipients.get(0);
            EmailLogInternalRequest logRequest = new EmailLogInternalRequest(
                    clientId == null ? null : clientId.longValue(),
                    poId,
                    subject,
                    recipientName,
                    firstRecipient,
                    senderUserId,
                    status,
                    docTypes == null ? List.of() : docTypes,
                    List.of(),
                    attachmentFilenames == null ? List.of() : attachmentFilenames
            );
            activityFeignClient.createEmailLog(logRequest);
        } catch (Exception e) {
            log.warn("자동 메일 이력 기록 실패 — subject={}, reason={}", subject, e.getMessage());
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
