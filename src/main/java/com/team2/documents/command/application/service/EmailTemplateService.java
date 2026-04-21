package com.team2.documents.command.application.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final SpringTemplateEngine templateEngine;

    public String renderDocumentEmail(DocumentEmailModel model) {
        Context context = new Context();
        context.setVariable("title", model.title());
        context.setVariable("recipientName", blankToFallback(model.recipientName(), "Customer"));
        context.setVariable("headline", model.headline());
        context.setVariable("message", model.message());
        context.setVariable("documentNo", model.documentNo());
        context.setVariable("documentTypes", model.documentTypes() == null ? List.of() : model.documentTypes());
        context.setVariable("attachmentFilenames", model.attachmentFilenames() == null ? List.of() : model.attachmentFilenames());
        context.setVariable("footerNote", model.footerNote());
        context.setVariable("sentDate", LocalDate.now(KOREA_ZONE));
        return templateEngine.process("mail/document-email", context);
    }

    private String blankToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    public record DocumentEmailModel(
            String title,
            String recipientName,
            String headline,
            String message,
            String documentNo,
            List<String> documentTypes,
            List<String> attachmentFilenames,
            String footerNote
    ) {
    }
}
