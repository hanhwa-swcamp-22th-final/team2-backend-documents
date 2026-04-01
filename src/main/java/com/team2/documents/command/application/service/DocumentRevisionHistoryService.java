package com.team2.documents.command.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;

@Service
@Transactional
public class DocumentRevisionHistoryService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final DocumentJsonSupportService documentJsonSupportService;

    public DocumentRevisionHistoryService(PurchaseOrderCommandService purchaseOrderCommandService,
                                          ProformaInvoiceCommandService proformaInvoiceCommandService,
                                          DocumentJsonSupportService documentJsonSupportService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.documentJsonSupportService = documentJsonSupportService;
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
        purchaseOrder.setRevisionHistory(documentJsonSupportService.appendRevision(
                purchaseOrder.getRevisionHistory(),
                action,
                actorUserId,
                status,
                message,
                LocalDateTime.now(),
                changes
        ));
        purchaseOrderCommandService.save(purchaseOrder);
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
        proformaInvoice.setRevisionHistory(documentJsonSupportService.appendRevision(
                proformaInvoice.getRevisionHistory(),
                action,
                actorUserId,
                status,
                message,
                LocalDateTime.now(),
                changes
        ));
        proformaInvoiceCommandService.save(proformaInvoice);
    }
}
