package com.team2.documents.query.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.query.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.query.service.ApprovalRequestQueryService;
import com.team2.documents.query.service.CommercialInvoiceQueryService;
import com.team2.documents.query.service.PackingListQueryService;
import com.team2.documents.query.service.ProformaInvoiceQueryService;
import com.team2.documents.query.service.PurchaseOrderQueryService;
import com.team2.documents.query.service.ProductionOrderQueryService;
import com.team2.documents.query.service.ShipmentOrderQueryService;
import com.team2.documents.query.service.ShipmentQueryService;
import com.team2.documents.query.service.CollectionQueryService;

@RestController
@RequestMapping("/api")
public class DocumentQueryController {

    private final PurchaseOrderQueryService purchaseOrderQueryService;
    private final ProformaInvoiceQueryService proformaInvoiceQueryService;
    private final CommercialInvoiceQueryService commercialInvoiceQueryService;
    private final PackingListQueryService packingListQueryService;
    private final ShipmentOrderQueryService shipmentOrderQueryService;
    private final ProductionOrderQueryService productionOrderQueryService;
    private final ShipmentQueryService shipmentQueryService;
    private final CollectionQueryService collectionQueryService;
    private final ApprovalRequestQueryService approvalRequestQueryService;

    public DocumentQueryController(PurchaseOrderQueryService purchaseOrderQueryService,
                                   ProformaInvoiceQueryService proformaInvoiceQueryService,
                                   CommercialInvoiceQueryService commercialInvoiceQueryService,
                                   PackingListQueryService packingListQueryService,
                                   ShipmentOrderQueryService shipmentOrderQueryService,
                                   ProductionOrderQueryService productionOrderQueryService,
                                   ShipmentQueryService shipmentQueryService,
                                   CollectionQueryService collectionQueryService,
                                   ApprovalRequestQueryService approvalRequestQueryService) {
        this.purchaseOrderQueryService = purchaseOrderQueryService;
        this.proformaInvoiceQueryService = proformaInvoiceQueryService;
        this.commercialInvoiceQueryService = commercialInvoiceQueryService;
        this.packingListQueryService = packingListQueryService;
        this.shipmentOrderQueryService = shipmentOrderQueryService;
        this.productionOrderQueryService = productionOrderQueryService;
        this.shipmentQueryService = shipmentQueryService;
        this.collectionQueryService = collectionQueryService;
        this.approvalRequestQueryService = approvalRequestQueryService;
    }

    @GetMapping("/proforma-invoices")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.ProformaInvoice>> getProformaInvoices() {
        return ResponseEntity.ok(proformaInvoiceQueryService.findAll());
    }

    @GetMapping("/proforma-invoices/{piId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.ProformaInvoice> getProformaInvoice(@PathVariable String piId) {
        return ResponseEntity.ok(proformaInvoiceQueryService.findById(piId));
    }

    @GetMapping("/commercial-invoices")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.CommercialInvoice>> getCommercialInvoices() {
        return ResponseEntity.ok(commercialInvoiceQueryService.findAll());
    }

    @GetMapping("/commercial-invoices/{ciId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.CommercialInvoice> getCommercialInvoice(@PathVariable String ciId) {
        return ResponseEntity.ok(commercialInvoiceQueryService.findById(ciId));
    }

    @GetMapping("/packing-lists")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.PackingList>> getPackingLists() {
        return ResponseEntity.ok(packingListQueryService.findAll());
    }

    @GetMapping("/packing-lists/{plId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.PackingList> getPackingList(@PathVariable String plId) {
        return ResponseEntity.ok(packingListQueryService.findById(plId));
    }

    @GetMapping("/shipment-orders")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.ShipmentOrder>> getShipmentOrders() {
        return ResponseEntity.ok(shipmentOrderQueryService.findAll());
    }

    @GetMapping("/shipment-orders/{shipmentOrderId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.ShipmentOrder> getShipmentOrder(
            @PathVariable String shipmentOrderId) {
        return ResponseEntity.ok(shipmentOrderQueryService.findById(shipmentOrderId));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.PurchaseOrder>> getPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderQueryService.findAll());
    }

    @GetMapping("/purchase-orders/{poId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.PurchaseOrder> getPurchaseOrder(@PathVariable String poId) {
        return ResponseEntity.ok(purchaseOrderQueryService.findById(poId));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<PurchaseOrderInitialStatusResponse> determineInitialStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new PurchaseOrderInitialStatusResponse(
                purchaseOrderQueryService.determineInitialStatus(userId)));
    }

    @GetMapping("/approval-requests")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.ApprovalRequest>> getApprovalRequests() {
        return ResponseEntity.ok(approvalRequestQueryService.findAll());
    }

    @GetMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.ApprovalRequest> getApprovalRequest(@PathVariable Long approvalRequestId) {
        return ResponseEntity.ok(approvalRequestQueryService.findById(approvalRequestId));
    }

    @GetMapping("/approval-requests/document/{documentType}/{documentId}/status/{status}")
    public ResponseEntity<com.team2.documents.command.domain.entity.ApprovalRequest> getApprovalRequestByDocumentAndStatus(
            @PathVariable String documentType,
            @PathVariable String documentId,
            @PathVariable String status) {
        return ResponseEntity.ok(
                approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status));
    }

    @GetMapping("/production-orders")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.ProductionOrder>> getProductionOrders() {
        return ResponseEntity.ok(productionOrderQueryService.findAll());
    }

    @GetMapping("/production-orders/{productionOrderId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.ProductionOrder> getProductionOrder(
            @PathVariable String productionOrderId) {
        return ResponseEntity.ok(productionOrderQueryService.findById(productionOrderId));
    }

    @GetMapping("/shipments")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.Shipment>> getShipments() {
        return ResponseEntity.ok(shipmentQueryService.findAll());
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.Shipment> getShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(shipmentQueryService.findById(shipmentId));
    }

    @GetMapping("/collections")
    public ResponseEntity<java.util.List<com.team2.documents.command.domain.entity.Collection>> getCollections() {
        return ResponseEntity.ok(collectionQueryService.findAll());
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<com.team2.documents.command.domain.entity.Collection> getCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(collectionQueryService.findById(collectionId));
    }
}
