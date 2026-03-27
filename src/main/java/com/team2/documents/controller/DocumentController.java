package com.team2.documents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.dto.PurchaseOrderCreateRequest;
import com.team2.documents.dto.PurchaseOrderCreateResponse;
import com.team2.documents.dto.PurchaseOrderDeletionRequest;
import com.team2.documents.dto.PurchaseOrderDeletionResponse;
import com.team2.documents.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.dto.PurchaseOrderModificationRequest;
import com.team2.documents.dto.PurchaseOrderModificationResponse;
import com.team2.documents.dto.PurchaseOrderRegistrationRequest;
import com.team2.documents.dto.PurchaseOrderRegistrationResponse;
import com.team2.documents.dto.CollectionUpdateRequest;
import com.team2.documents.dto.ApprovalRequestCreateRequest;
import com.team2.documents.dto.ApprovalRequestUpdateRequest;
import com.team2.documents.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.dto.ProformaInvoiceRegistrationResponse;
import com.team2.documents.service.ApprovalRequestCommandService;
import com.team2.documents.service.CollectionQueryService;
import com.team2.documents.service.CollectionCommandService;
import com.team2.documents.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.service.ProformaInvoiceApprovalWorkflowService;
import com.team2.documents.service.ProformaInvoiceRejectionWorkflowService;
import com.team2.documents.service.ProformaInvoiceService;
import com.team2.documents.service.ProductionOrderQueryService;
import com.team2.documents.service.ShipmentQueryService;
import com.team2.documents.service.ShipmentCommandService;
import com.team2.documents.service.PurchaseOrderModificationService;
import com.team2.documents.service.PurchaseOrderModificationRequestService;
import com.team2.documents.service.PurchaseOrderDeletionRequestService;
import com.team2.documents.service.PurchaseOrderApprovalWorkflowService;
import com.team2.documents.service.PurchaseOrderCreationService;
import com.team2.documents.service.PurchaseOrderDocumentGenerationService;
import com.team2.documents.service.PurchaseOrderProductionOrderGenerationService;
import com.team2.documents.service.PurchaseOrderRejectionWorkflowService;
import com.team2.documents.service.PurchaseOrderRegistrationService;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;
    private final ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;
    private final PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;
    private final PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;
    private final PurchaseOrderModificationRequestService purchaseOrderModificationRequestService;
    private final PurchaseOrderDeletionRequestService purchaseOrderDeletionRequestService;
    private final PurchaseOrderCreationService purchaseOrderCreationService;
    private final PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;
    private final PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService;
    private final PurchaseOrderRegistrationService purchaseOrderRegistrationService;
    private final ProformaInvoiceService proformaInvoiceService;
    private final ProductionOrderQueryService productionOrderQueryService;
    private final ShipmentQueryService shipmentQueryService;
    private final CollectionQueryService collectionQueryService;
    private final ShipmentCommandService shipmentCommandService;
    private final CollectionCommandService collectionCommandService;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public DocumentController(PurchaseOrderModificationService purchaseOrderModificationService,
                              ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService,
                              ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService,
                              PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService,
                              PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService,
                              PurchaseOrderModificationRequestService purchaseOrderModificationRequestService,
                              PurchaseOrderDeletionRequestService purchaseOrderDeletionRequestService,
                              PurchaseOrderCreationService purchaseOrderCreationService,
                              PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService,
                              PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService,
                              PurchaseOrderRegistrationService purchaseOrderRegistrationService,
                              ProformaInvoiceService proformaInvoiceService,
                              ProductionOrderQueryService productionOrderQueryService,
                              ShipmentQueryService shipmentQueryService,
                              CollectionQueryService collectionQueryService,
                              ShipmentCommandService shipmentCommandService,
                              CollectionCommandService collectionCommandService,
                              ApprovalRequestCommandService approvalRequestCommandService) {
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.proformaInvoiceApprovalWorkflowService = proformaInvoiceApprovalWorkflowService;
        this.proformaInvoiceRejectionWorkflowService = proformaInvoiceRejectionWorkflowService;
        this.purchaseOrderApprovalWorkflowService = purchaseOrderApprovalWorkflowService;
        this.purchaseOrderRejectionWorkflowService = purchaseOrderRejectionWorkflowService;
        this.purchaseOrderModificationRequestService = purchaseOrderModificationRequestService;
        this.purchaseOrderDeletionRequestService = purchaseOrderDeletionRequestService;
        this.purchaseOrderCreationService = purchaseOrderCreationService;
        this.purchaseOrderDocumentGenerationService = purchaseOrderDocumentGenerationService;
        this.purchaseOrderProductionOrderGenerationService = purchaseOrderProductionOrderGenerationService;
        this.purchaseOrderRegistrationService = purchaseOrderRegistrationService;
        this.proformaInvoiceService = proformaInvoiceService;
        this.productionOrderQueryService = productionOrderQueryService;
        this.shipmentQueryService = shipmentQueryService;
        this.collectionQueryService = collectionQueryService;
        this.shipmentCommandService = shipmentCommandService;
        this.collectionCommandService = collectionCommandService;
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrderCreateResponse> create(@RequestBody PurchaseOrderCreateRequest request) {
        purchaseOrderCreationService.create(request.userId());
        return ResponseEntity.ok(new PurchaseOrderCreateResponse("PO 생성 요청이 처리되었습니다."));
    }

    @PostMapping("/proforma-invoices/request-registration")
    public ResponseEntity<ProformaInvoiceRegistrationResponse> requestRegistration(
            @RequestBody ProformaInvoiceRegistrationRequest request) {
        proformaInvoiceService.requestRegistration(request.piId(), request.userId());
        return ResponseEntity.ok(new ProformaInvoiceRegistrationResponse("PI 등록 요청이 처리되었습니다."));
    }

    @PostMapping("/purchase-orders/request-registration")
    public ResponseEntity<PurchaseOrderRegistrationResponse> requestPurchaseOrderRegistration(
            @RequestBody PurchaseOrderRegistrationRequest request) {
        purchaseOrderRegistrationService.requestRegistration(request.poId(), request.userId());
        return ResponseEntity.ok(new PurchaseOrderRegistrationResponse("PO 등록 요청이 처리되었습니다."));
    }

    @PostMapping("/purchase-orders/request-modification")
    public ResponseEntity<PurchaseOrderModificationResponse> requestPurchaseOrderModification(
            @RequestBody PurchaseOrderModificationRequest request) {
        purchaseOrderModificationRequestService.requestModification(request.poId(), request.userId());
        return ResponseEntity.ok(new PurchaseOrderModificationResponse("PO 수정 요청이 처리되었습니다."));
    }

    @PostMapping("/purchase-orders/request-deletion")
    public ResponseEntity<PurchaseOrderDeletionResponse> requestPurchaseOrderDeletion(
            @RequestBody PurchaseOrderDeletionRequest request) {
        purchaseOrderDeletionRequestService.requestDeletion(request.poId(), request.userId());
        return ResponseEntity.ok(new PurchaseOrderDeletionResponse("PO 삭제 요청이 처리되었습니다."));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<PurchaseOrderInitialStatusResponse> determineInitialStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new PurchaseOrderInitialStatusResponse(
                purchaseOrderCreationService.determineInitialStatus(userId)));
    }

    @GetMapping("/production-orders")
    public ResponseEntity<java.util.List<com.team2.documents.entity.ProductionOrder>> getProductionOrders() {
        return ResponseEntity.ok(productionOrderQueryService.findAll());
    }

    @GetMapping("/production-orders/{id}")
    public ResponseEntity<com.team2.documents.entity.ProductionOrder> getProductionOrder(@PathVariable Long id) {
        return ResponseEntity.ok(productionOrderQueryService.findById(id));
    }

    @GetMapping("/shipments")
    public ResponseEntity<java.util.List<com.team2.documents.entity.Shipment>> getShipments() {
        return ResponseEntity.ok(shipmentQueryService.findAll());
    }

    @GetMapping("/shipments/{id}")
    public ResponseEntity<com.team2.documents.entity.Shipment> getShipment(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentQueryService.findById(id));
    }

    @PutMapping("/shipments/{id}")
    public ResponseEntity<com.team2.documents.entity.Shipment> updateShipmentStatus(@PathVariable Long id,
                                                                                     @RequestBody ShipmentStatusUpdateRequest request) {
        return ResponseEntity.ok(shipmentCommandService.updateStatus(id, request.status()));
    }

    @GetMapping("/collections")
    public ResponseEntity<java.util.List<com.team2.documents.entity.Collection>> getCollections() {
        return ResponseEntity.ok(collectionQueryService.findAll());
    }

    @GetMapping("/collections/{id}")
    public ResponseEntity<com.team2.documents.entity.Collection> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionQueryService.findById(id));
    }

    @PutMapping("/collections/{id}")
    public ResponseEntity<com.team2.documents.entity.Collection> updateCollection(@PathVariable Long id,
                                                                                  @RequestBody CollectionUpdateRequest request) {
        return ResponseEntity.ok(collectionCommandService.complete(id, request.status(), request.collectionCompletedDate()));
    }

    @PostMapping("/approval-requests")
    public ResponseEntity<com.team2.documents.entity.ApprovalRequest> createApprovalRequest(
            @RequestBody ApprovalRequestCreateRequest request) {
        return ResponseEntity.ok(approvalRequestCommandService.create(request));
    }

    @PutMapping("/approval-requests/{id}")
    public ResponseEntity<com.team2.documents.entity.ApprovalRequest> updateApprovalRequest(@PathVariable Long id,
                                                                                             @RequestBody ApprovalRequestUpdateRequest request) {
        return ResponseEntity.ok(approvalRequestCommandService.update(id, request.status()));
    }

    @PostMapping("/purchase-orders/{poId}/validate-modifiable")
    public ResponseEntity<Void> validateModifiable(@PathVariable String poId) {
        purchaseOrderModificationService.validateModifiable(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/validate-deletable")
    public ResponseEntity<Void> validateDeletable(@PathVariable String poId) {
        purchaseOrderModificationService.validateDeletable(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/generate-documents")
    public ResponseEntity<Void> generateDocuments(@PathVariable String poId) {
        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/generate-production-order")
    public ResponseEntity<Void> generateProductionOrder(@PathVariable String poId) {
        purchaseOrderProductionOrderGenerationService.generate(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/approve")
    public ResponseEntity<Void> approvePurchaseOrder(@PathVariable String poId) {
        purchaseOrderApprovalWorkflowService.approve(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/reject")
    public ResponseEntity<Void> rejectPurchaseOrder(@PathVariable String poId) {
        purchaseOrderRejectionWorkflowService.reject(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/proforma-invoices/{piId}/approve")
    public ResponseEntity<Void> approveProformaInvoice(@PathVariable String piId) {
        proformaInvoiceApprovalWorkflowService.approve(piId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/proforma-invoices/{piId}/reject")
    public ResponseEntity<Void> rejectProformaInvoice(@PathVariable String piId) {
        proformaInvoiceRejectionWorkflowService.reject(piId);
        return ResponseEntity.ok().build();
    }
}
