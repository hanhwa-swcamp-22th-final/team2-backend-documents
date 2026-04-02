package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentLinkBuilderService {

    private final DocumentJsonSupportService documentJsonSupportService;

    public DocumentLinkBuilderService(DocumentJsonSupportService documentJsonSupportService) {
        this.documentJsonSupportService = documentJsonSupportService;
    }

    public String append(String existingLinks, String documentId, String documentType, String status) {
        return documentJsonSupportService.appendLinkedDocument(existingLinks, documentId, documentType, status);
    }
}
