package com.team2.documents.command.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.team2.documents.command.application.dto.EmailLogInternalRequest;
import com.team2.documents.command.application.dto.EmailSendRequest;
import com.team2.documents.command.application.dto.EmailSendResponse;
import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.infrastructure.client.ActivityFeignClient;
import com.team2.documents.infrastructure.pdf.PdfGenerationService;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSendService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;
    private final PackingListJpaRepository packingListJpaRepository;
    private final ShipmentOrderJpaRepository shipmentOrderJpaRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final PdfGenerationService pdfGenerationService;
    private final JavaMailSender javaMailSender;
    private final ActivityFeignClient activityFeignClient;
    @Value("${spring.mail.username:}")
    private String mailUsername;

    /**
     * 사용자 트리거 호출 — 기본 경로. 발송 + Activity 로그 기록.
     * Frontend / Documents 자체 자동 발송 흐름에서 호출.
     */
    public EmailSendResponse sendEmail(Long userId, EmailSendRequest request) {
        return sendEmail(userId, request, true);
    }

    /**
     * 발송만 수행 — Activity 로그 기록은 건너뜀.
     * Activity 의 재전송 흐름(resend)에서 호출. Activity 는 자기 EmailLog 의 상태를 직접 갱신하므로
     * Documents 가 별도로 로그를 INSERT 하면 중복 row 가 생김. 이 변형은 그 문제를 방지한다.
     */
    public EmailSendResponse sendEmailWithoutLogging(Long userId, EmailSendRequest request) {
        return sendEmail(userId, request, false);
    }

    /**
     * 핵심 구현 — shouldLog 로 Activity 연동 여부를 분기한다.
     *
     * <p>Write Ownership 원칙:
     * <ul>
     *   <li>사용자 트리거 (CI/PL 상세 → 메일 발송 버튼) → shouldLog=true : Documents 가 Activity 에 새 로그 기록
     *   <li>Activity 재전송 (EmailLog resend) → shouldLog=false : Activity 가 기존 로그를 update 만 수행
     * </ul>
     * 두 경로가 서로 다른 write owner 를 가지므로 이중 write 로 인한 중복 row 가 발생하지 않는다.
     */
    public EmailSendResponse sendEmail(Long userId, EmailSendRequest request, boolean shouldLog) {
        List<String> attachmentFilenames = new ArrayList<>();
        List<byte[]> pdfDataList = new ArrayList<>();

        String poId = request.poId();

        PurchaseOrder po = null;
        if (poId != null && !poId.isBlank()) {
            po = purchaseOrderRepository.findByPoCode(poId).orElse(null);
        }

        for (String docType : request.docTypes()) {
            try {
                byte[] pdfData = generatePdfForDocType(docType, po, poId);
                if (pdfData == null) {
                    log.warn("Could not generate PDF for docType={}, poId={}", docType, poId);
                    continue;
                }

                String filename = docType + ".pdf";
                attachmentFilenames.add(filename);
                pdfDataList.add(pdfData);
            } catch (Exception e) {
                log.error("Failed to generate PDF for docType={}", docType, e);
            }
        }

        if (attachmentFilenames.isEmpty()) {
            if (shouldLog) {
                logToActivity(request, userId, "FAILED", attachmentFilenames);
            }
            return new EmailSendResponse("FAILED", "No documents could be generated", attachmentFilenames);
        }

        try {
            sendMimeMessage(request, attachmentFilenames, pdfDataList);
            if (shouldLog) {
                logToActivity(request, userId, "SENT", attachmentFilenames);
            }
            return new EmailSendResponse("SENT",
                    "Email sent successfully with " + attachmentFilenames.size() + " attachment(s)",
                    attachmentFilenames);
        } catch (Exception e) {
            log.error("Failed to send email to {}", request.emailRecipientEmail(), e);
            if (shouldLog) {
                logToActivity(request, userId, "FAILED", attachmentFilenames);
            }
            return new EmailSendResponse("FAILED", "Failed to send email: " + e.getMessage(), attachmentFilenames);
        }
    }

    private byte[] generatePdfForDocType(String docType, PurchaseOrder po, String poId) {
        Long poDbId = po != null ? po.getPurchaseOrderId() : null;

        switch (docType.toUpperCase()) {
            case "PO":
                if (po == null) return null;
                return pdfGenerationService.generatePurchaseOrderPdf(po, po.getItems());

            case "PI":
                if (po == null || po.getPiId() == null) return null;
                ProformaInvoice pi = proformaInvoiceRepository.findByPiCode(po.getPiId()).orElse(null);
                if (pi == null) return null;
                return pdfGenerationService.generateProformaInvoicePdf(pi, pi.getItems());

            case "CI":
                if (poDbId == null) return null;
                CommercialInvoice ci = commercialInvoiceJpaRepository.findByPoId(poDbId).orElse(null);
                if (ci == null) return null;
                return pdfGenerationService.generateCommercialInvoicePdf(ci);

            case "PL":
                if (poDbId == null) return null;
                PackingList pl = packingListJpaRepository.findByPoId(poDbId).orElse(null);
                if (pl == null) return null;
                return pdfGenerationService.generatePackingListPdf(pl);

            case "SHIPPING_ORDER":
                if (poDbId == null) return null;
                ShipmentOrder so = shipmentOrderJpaRepository.findByPoId(poDbId).orElse(null);
                if (so == null) return null;
                return pdfGenerationService.generateShipmentOrderPdf(so);

            case "PRODUCTION_ORDER":
                if (poDbId == null) return null;
                ProductionOrder prodOrder = productionOrderRepository.findByPoId(poDbId).orElse(null);
                if (prodOrder == null) return null;
                return pdfGenerationService.generateProductionOrderPdf(prodOrder);

            default:
                log.warn("Unknown document type: {}", docType);
                return null;
        }
    }

    private void sendMimeMessage(EmailSendRequest request,
                                 List<String> attachmentFilenames,
                                 List<byte[]> pdfDataList) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        if (mailUsername == null || mailUsername.isBlank()) {
            throw new IllegalStateException("spring.mail.username 미설정 — 메일 발송 불가.");
        }
        helper.setFrom(mailUsername);
        helper.setTo(request.emailRecipientEmail());
        helper.setSubject(request.emailTitle());

        String body = "Dear " + (request.emailRecipientName() != null ? request.emailRecipientName() : "") + ",\n\n"
                + "Please find the attached trade documents.\n\n"
                + "Best regards";
        helper.setText(body);

        for (int i = 0; i < attachmentFilenames.size(); i++) {
            DataSource dataSource = new ByteArrayDataSource(pdfDataList.get(i), "application/pdf");
            helper.addAttachment(attachmentFilenames.get(i), dataSource);
        }

        javaMailSender.send(message);
        log.info("Email sent to {} with {} attachments", request.emailRecipientEmail(), attachmentFilenames.size());
    }

    private void logToActivity(EmailSendRequest request, Long userId, String status,
                               List<String> attachmentFilenames) {
        try {
            EmailLogInternalRequest logRequest = new EmailLogInternalRequest(
                    request.clientId(),
                    request.poId(),
                    request.emailTitle(),
                    request.emailRecipientName(),
                    request.emailRecipientEmail(),
                    userId,
                    status,
                    request.docTypes(),
                    List.of(),
                    attachmentFilenames
            );
            activityFeignClient.createEmailLog(logRequest);
        } catch (Exception e) {
            log.warn("Failed to log email activity to Activity service", e);
        }
    }
}
