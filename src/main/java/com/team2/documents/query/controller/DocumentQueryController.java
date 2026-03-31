package com.team2.documents.query.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.query.service.PurchaseOrderQueryService;
import com.team2.documents.query.service.ProductionOrderQueryService;
import com.team2.documents.query.service.ShipmentQueryService;
import com.team2.documents.query.service.CollectionQueryService;

@RestController
@RequestMapping("/api")
public class DocumentQueryController {

    private final PurchaseOrderQueryService purchaseOrderQueryService;
    private final ProductionOrderQueryService productionOrderQueryService;
    private final ShipmentQueryService shipmentQueryService;
    private final CollectionQueryService collectionQueryService;

    public DocumentQueryController(PurchaseOrderQueryService purchaseOrderQueryService,
                                   ProductionOrderQueryService productionOrderQueryService,
                                   ShipmentQueryService shipmentQueryService,
                                   CollectionQueryService collectionQueryService) {
        this.purchaseOrderQueryService = purchaseOrderQueryService;
        this.productionOrderQueryService = productionOrderQueryService;
        this.shipmentQueryService = shipmentQueryService;
        this.collectionQueryService = collectionQueryService;
    }

    @GetMapping("/purchase-orders/{poId}")
    public ResponseEntity<com.team2.documents.entity.PurchaseOrder> getPurchaseOrder(@PathVariable String poId) {
        return ResponseEntity.ok(purchaseOrderQueryService.findById(poId));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<PurchaseOrderInitialStatusResponse> determineInitialStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new PurchaseOrderInitialStatusResponse(
                purchaseOrderQueryService.determineInitialStatus(userId)));
    }

    @GetMapping("/production-orders")
    public ResponseEntity<java.util.List<com.team2.documents.entity.ProductionOrder>> getProductionOrders() {
        return ResponseEntity.ok(productionOrderQueryService.findAll());
    }

    @GetMapping("/production-orders/{productionOrderId}")
    public ResponseEntity<com.team2.documents.entity.ProductionOrder> getProductionOrder(
            @PathVariable String productionOrderId) {
        return ResponseEntity.ok(productionOrderQueryService.findById(productionOrderId));
    }

    @GetMapping("/shipments")
    public ResponseEntity<java.util.List<com.team2.documents.entity.Shipment>> getShipments() {
        return ResponseEntity.ok(shipmentQueryService.findAll());
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<com.team2.documents.entity.Shipment> getShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(shipmentQueryService.findById(shipmentId));
    }

    @GetMapping("/collections")
    public ResponseEntity<java.util.List<com.team2.documents.entity.Collection>> getCollections() {
        return ResponseEntity.ok(collectionQueryService.findAll());
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<com.team2.documents.entity.Collection> getCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(collectionQueryService.findById(collectionId));
    }
}
