package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.application.dto.EmailLogInternalRequest;
import com.team2.documents.command.domain.entity.DocumentEmailOutbox;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.repository.DocumentEmailOutboxRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.infrastructure.client.ActivityFeignClient;
import com.team2.documents.infrastructure.pdf.PdfGenerationService;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentEmailOutboxWorker {

    private final DocumentEmailOutboxRepository outboxRepository;
    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final ShipmentOrderJpaRepository shipmentOrderJpaRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final AutoEmailRecipientResolver autoEmailRecipientResolver;
    private final PdfGenerationService pdfGenerationService;
    private final JavaMailSender javaMailSender;
    private final ActivityFeignClient activityFeignClient;
    private final EmailTemplateService emailTemplateService;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Async("documentMailTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAsync(Long outboxId) {
        DocumentEmailOutbox outbox = outboxRepository.findByIdForUpdate(outboxId).orElse(null);
        if (outbox == null) {
            log.warn("자동 메일 outbox row를 찾지 못했습니다. outboxId={}", outboxId);
            return;
        }
        if (!outbox.isReadyToProcess(DocumentEmailOutbox.now())) {
            return;
        }

        outbox.markProcessing();
        try {
            dispatch(outbox);
            outbox.markSent();
            log.info("자동 메일 outbox 처리 완료. outboxId={}, type={}, documentCode={}",
                    outbox.getId(),
                    outbox.getEventType(),
                    outbox.getDocumentCode());
        } catch (Exception e) {
            outbox.markFailed(e.getMessage());
            log.warn("자동 메일 outbox 처리 실패. outboxId={}, type={}, attempts={}/{}",
                    outbox.getId(),
                    outbox.getEventType(),
                    outbox.getAttempts(),
                    outbox.getMaxAttempts(),
                    e);
        }
    }

    private void dispatch(DocumentEmailOutbox outbox) {
        switch (outbox.getEventType()) {
            case APPROVED_PI_TO_BUYER -> sendApprovedPiToBuyer(outbox.getDocumentCode());
            case SHIPMENT_ORDER_TO_SHIPPING_TEAM -> sendShipmentOrderToShippingTeam(outbox.getDocumentCode());
            case PRODUCTION_ORDER_TO_PRODUCTION_TEAM -> sendProductionOrderToProductionTeam(outbox.getDocumentCode());
            default -> throw new IllegalArgumentException("Unknown auto mail event type: " + outbox.getEventType());
        }
    }

    private void sendApprovedPiToBuyer(String piCode) {
        ProformaInvoice proformaInvoice = proformaInvoiceRepository.findByPiCode(piCode)
                .orElseThrow(() -> new IllegalArgumentException("PI를 찾을 수 없습니다: " + piCode));
        AutoEmailRecipientResolver.BuyerRecipients buyerRecipients =
                autoEmailRecipientResolver.findBuyerRecipientsByClientId(proformaInvoice.getClientId());
        if (buyerRecipients.emails().isEmpty()) {
            log.warn("PI 자동 메일 수신자(바이어)를 찾지 못했습니다. piId={}", proformaInvoice.getPiId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateProformaInvoicePdf(proformaInvoice, proformaInvoice.getItems());
        String subject = "Proforma Invoice " + proformaInvoice.getPiId() + " - Approved";
        sendEmail(
                buyerRecipients.emails(),
                subject,
                emailTemplateService.renderDocumentEmail(new EmailTemplateService.DocumentEmailModel(
                        subject,
                        buyerRecipients.primaryName(),
                        "Approved Proforma Invoice is attached",
                        "The approved Proforma Invoice PDF is attached for your review.",
                        proformaInvoice.getPiId(),
                        List.of("PI"),
                        List.of("PI.pdf"),
                        "This email was automatically sent after PI approval."
                )),
                "PI.pdf",
                pdf
        );
        logEmailToActivity(
                proformaInvoice.getClientId(),
                null,
                subject,
                buyerRecipients.primaryName(),
                buyerRecipients.emails(),
                proformaInvoice.getManagerId(),
                "SENT",
                List.of("PI"),
                List.of("PI.pdf")
        );
    }

    private void sendShipmentOrderToShippingTeam(String shipmentOrderCode) {
        ShipmentOrder shipmentOrder = shipmentOrderJpaRepository.findByShipmentOrderCode(shipmentOrderCode)
                .orElseThrow(() -> new IllegalArgumentException("출하지시서를 찾을 수 없습니다: " + shipmentOrderCode));
        List<String> recipients = autoEmailRecipientResolver.findShippingTeamEmails();
        if (recipients.isEmpty()) {
            log.warn("출하팀 자동 메일 수신자를 찾지 못했습니다. shipmentOrderId={}",
                    shipmentOrder.getShipmentOrderId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateShipmentOrderPdf(shipmentOrder);
        String subject = "[자동발송] 출하지시서 생성 완료 - " + shipmentOrder.getShipmentOrderId();
        sendEmail(
                recipients,
                subject,
                emailTemplateService.renderDocumentEmail(new EmailTemplateService.DocumentEmailModel(
                        subject,
                        "출하팀",
                        "출하지시서가 생성되었습니다",
                        "PO 기준 출하지시서가 생성되어 확인용 PDF를 첨부합니다.",
                        shipmentOrder.getShipmentOrderId(),
                        List.of("SHIPPING_ORDER"),
                        List.of("SHIPPING_ORDER.pdf"),
                        "이 메일은 SalesBoost 문서 서비스에서 자동 발송되었습니다."
                )),
                "SHIPPING_ORDER.pdf",
                pdf
        );
    }

    private void sendProductionOrderToProductionTeam(String productionOrderCode) {
        ProductionOrder productionOrder = productionOrderRepository.findByProductionOrderCode(productionOrderCode)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서를 찾을 수 없습니다: " + productionOrderCode));
        List<String> recipients = autoEmailRecipientResolver.findProductionTeamEmails();
        if (recipients.isEmpty()) {
            log.warn("생산팀 자동 메일 수신자를 찾지 못했습니다. productionOrderId={}",
                    productionOrder.getProductionOrderId());
            return;
        }

        byte[] pdf = pdfGenerationService.generateProductionOrderPdf(productionOrder);
        String subject = "[자동발송] 생산지시서 생성 완료 - " + productionOrder.getProductionOrderId();
        sendEmail(
                recipients,
                subject,
                emailTemplateService.renderDocumentEmail(new EmailTemplateService.DocumentEmailModel(
                        subject,
                        "생산팀",
                        "생산지시서가 생성되었습니다",
                        "PO 기준 생산지시서가 생성되어 확인용 PDF를 첨부합니다.",
                        productionOrder.getProductionOrderId(),
                        List.of("PRODUCTION_ORDER"),
                        List.of("PRODUCTION_ORDER.pdf"),
                        "이 메일은 SalesBoost 문서 서비스에서 자동 발송되었습니다."
                )),
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
            helper.setText(body, true);
            DataSource dataSource = new ByteArrayDataSource(attachmentData, "application/pdf");
            helper.addAttachment(attachmentFilename, dataSource);
            javaMailSender.send(message);
            log.info("자동 메일 발송 완료. recipients={}, subject={}", recipients, subject);
        } catch (Exception e) {
            throw new IllegalStateException("자동 메일 발송에 실패했습니다. subject=" + subject, e);
        }
    }

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
            log.warn("자동 메일 이력 기록 실패 - subject={}, reason={}", subject, e.getMessage());
        }
    }
}
