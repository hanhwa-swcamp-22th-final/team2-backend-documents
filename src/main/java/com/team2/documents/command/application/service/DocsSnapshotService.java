package com.team2.documents.command.application.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.DocsRevision;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.repository.DocsRevisionRepository;

@Service
@Transactional
public class DocsSnapshotService {

    private final DocsRevisionRepository docsRevisionRepository;
    private final ObjectMapper objectMapper;

    public DocsSnapshotService(DocsRevisionRepository docsRevisionRepository, ObjectMapper objectMapper) {
        this.docsRevisionRepository = docsRevisionRepository;
        this.objectMapper = objectMapper;
    }

    public void savePurchaseOrderSnapshot(PurchaseOrder purchaseOrder) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("purchaseOrderId", purchaseOrder.getPurchaseOrderId());
        snapshot.put("poId", purchaseOrder.getPoId());
        snapshot.put("piId", purchaseOrder.getPiId());
        snapshot.put("issueDate", purchaseOrder.getIssueDate());
        snapshot.put("clientId", purchaseOrder.getClientId());
        snapshot.put("currencyId", purchaseOrder.getCurrencyId());
        snapshot.put("managerId", purchaseOrder.getManagerId());
        snapshot.put("status", purchaseOrder.getStatus() == null ? null : purchaseOrder.getStatus().name());
        snapshot.put("deliveryDate", purchaseOrder.getDeliveryDate());
        snapshot.put("incotermsCode", purchaseOrder.getIncotermsCode());
        snapshot.put("namedPlace", purchaseOrder.getNamedPlace());
        snapshot.put("totalAmount", purchaseOrder.getTotalAmount());
        snapshot.put("itemsSnapshot", purchaseOrder.getItemsSnapshot());
        persist("PO", purchaseOrder.getPurchaseOrderId(), snapshot);
    }

    public void saveCommercialInvoiceSnapshot(CommercialInvoice commercialInvoice, PurchaseOrder purchaseOrder) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("commercialInvoiceId", commercialInvoice.getCommercialInvoiceId());
        snapshot.put("ciId", commercialInvoice.getCiId());
        snapshot.put("poId", commercialInvoice.getPoId());
        snapshot.put("invoiceDate", commercialInvoice.getInvoiceDate());
        snapshot.put("clientId", commercialInvoice.getClientId());
        snapshot.put("currencyId", commercialInvoice.getCurrencyId());
        snapshot.put("totalAmount", commercialInvoice.getTotalAmount());
        snapshot.put("status", commercialInvoice.getStatus());
        snapshot.put("sourcePurchaseOrder", purchaseOrder.getPoId());
        snapshot.put("sourceItemsSnapshot", purchaseOrder.getItemsSnapshot());
        snapshot.put("sourceClientName", purchaseOrder.getClientName());
        persist("CI", commercialInvoice.getCommercialInvoiceId(), snapshot);
    }

    public void savePackingListSnapshot(PackingList packingList, PurchaseOrder purchaseOrder) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("packingListId", packingList.getPackingListId());
        snapshot.put("plId", packingList.getPlId());
        snapshot.put("poId", packingList.getPoId());
        snapshot.put("invoiceDate", packingList.getInvoiceDate());
        snapshot.put("clientId", packingList.getClientId());
        snapshot.put("grossWeight", packingList.getGrossWeight());
        snapshot.put("status", packingList.getStatus());
        snapshot.put("sourcePurchaseOrder", purchaseOrder.getPoId());
        snapshot.put("sourceItemsSnapshot", purchaseOrder.getItemsSnapshot());
        snapshot.put("sourceClientName", purchaseOrder.getClientName());
        persist("PL", packingList.getPackingListId(), snapshot);
    }

    private void persist(String docType, Long docId, Map<String, Object> snapshot) {
        try {
            docsRevisionRepository.save(new DocsRevision(docType, docId, objectMapper.writeValueAsString(snapshot)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("문서 스냅샷 저장에 실패했습니다.", exception);
        }
    }
}
