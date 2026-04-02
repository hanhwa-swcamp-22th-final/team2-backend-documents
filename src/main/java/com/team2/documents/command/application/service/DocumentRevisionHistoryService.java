package com.team2.documents.command.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.DocsRevision;
import com.team2.documents.command.domain.repository.DocsRevisionRepository;

@Service
@Transactional
public class DocumentRevisionHistoryService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final DocumentJsonSupportService documentJsonSupportService;
    private final DocsRevisionRepository docsRevisionRepository;
    private final ObjectMapper objectMapper;

    public DocumentRevisionHistoryService(PurchaseOrderCommandService purchaseOrderCommandService,
                                          ProformaInvoiceCommandService proformaInvoiceCommandService,
                                          DocumentJsonSupportService documentJsonSupportService,
                                          DocsRevisionRepository docsRevisionRepository,
                                          ObjectMapper objectMapper) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentJsonSupportService = documentJsonSupportService;
        this.docsRevisionRepository = docsRevisionRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> capturePurchaseOrderSnapshot(PurchaseOrder purchaseOrder) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("status", purchaseOrder.getStatus() == null ? null : purchaseOrder.getStatus().name());
        snapshot.put("approvalStatus", purchaseOrder.getApprovalStatus());
        snapshot.put("requestStatus", purchaseOrder.getRequestStatus());
        snapshot.put("approvalAction", purchaseOrder.getApprovalAction());
        snapshot.put("approvalRequestedBy", purchaseOrder.getApprovalRequestedBy());
        snapshot.put("approvalReview", purchaseOrder.getApprovalReview());
        snapshot.put("linkedDocuments", purchaseOrder.getLinkedDocuments());
        snapshot.put("itemsSnapshot", purchaseOrder.getItemsSnapshot());
        snapshot.put("totalAmount", purchaseOrder.getTotalAmount());
        return snapshot;
    }

    public Map<String, Object> captureProformaInvoiceSnapshot(ProformaInvoice proformaInvoice) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("status", proformaInvoice.getStatus() == null ? null : proformaInvoice.getStatus().name());
        snapshot.put("approvalStatus", proformaInvoice.getApprovalStatus());
        snapshot.put("requestStatus", proformaInvoice.getRequestStatus());
        snapshot.put("approvalAction", proformaInvoice.getApprovalAction());
        snapshot.put("approvalRequestedBy", proformaInvoice.getApprovalRequestedBy());
        snapshot.put("approvalReview", proformaInvoice.getApprovalReview());
        snapshot.put("linkedDocuments", proformaInvoice.getLinkedDocuments());
        snapshot.put("itemsSnapshot", proformaInvoice.getItemsSnapshot());
        snapshot.put("totalAmount", proformaInvoice.getTotalAmount());
        return snapshot;
    }

    public void recordPurchaseOrderEvent(String poId, String action, Long actorUserId, String status, String message) {
        recordPurchaseOrderEvent(poId, action, actorUserId, status, message, null);
    }

    public void recordPurchaseOrderEvent(String poId,
                                         String action,
                                         Long actorUserId,
                                         String status,
                                         String message,
                                         Map<String, Object> beforeSnapshot) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        Map<String, Object> changes = beforeSnapshot == null ? Map.of()
                : documentJsonSupportService.diffSnapshots(beforeSnapshot, capturePurchaseOrderSnapshot(purchaseOrder));
        persistRevisionEvent("PO", purchaseOrder.getPurchaseOrderId(), purchaseOrder.getPoId(),
                action, actorUserId, status, message, changes);
    }

    public void recordProformaInvoiceEvent(String piId, String action, Long actorUserId, String status, String message) {
        recordProformaInvoiceEvent(piId, action, actorUserId, status, message, null);
    }

    public void recordProformaInvoiceEvent(String piId,
                                           String action,
                                           Long actorUserId,
                                           String status,
                                           String message,
                                           Map<String, Object> beforeSnapshot) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);
        Map<String, Object> changes = beforeSnapshot == null ? Map.of()
                : documentJsonSupportService.diffSnapshots(beforeSnapshot, captureProformaInvoiceSnapshot(proformaInvoice));
        persistRevisionEvent("PI", proformaInvoice.getProformaInvoiceId(), proformaInvoice.getPiId(),
                action, actorUserId, status, message, changes);
    }

    private void persistRevisionEvent(String docType,
                                      Long docId,
                                      String docCode,
                                      String action,
                                      Long actorUserId,
                                      String status,
                                      String message,
                                      Map<String, Object> changes) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("entryType", "REVISION");
        event.put("docType", docType);
        event.put("docCode", docCode);
        event.put("action", action);
        event.put("actorUserId", actorUserId);
        event.put("status", status);
        event.put("message", message);
        event.put("at", LocalDateTime.now().toString());
        if (changes != null && !changes.isEmpty()) {
            event.put("changes", changes);
        }
        try {
            docsRevisionRepository.save(new DocsRevision(docType, docId, objectMapper.writeValueAsString(event)));
        } catch (Exception exception) {
            throw new IllegalStateException("문서 이력 저장에 실패했습니다.", exception);
        }
    }
}
