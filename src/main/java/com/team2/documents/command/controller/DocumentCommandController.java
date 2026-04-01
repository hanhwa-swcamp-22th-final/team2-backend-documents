package com.team2.documents.command.controller;

import org.springframework.http.ResponseEntity;
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
import com.team2.documents.dto.PurchaseOrderModificationRequest;
import com.team2.documents.dto.PurchaseOrderModificationResponse;
import com.team2.documents.dto.PurchaseOrderRegistrationRequest;
import com.team2.documents.dto.PurchaseOrderRegistrationResponse;
import com.team2.documents.dto.CollectionUpdateRequest;
import com.team2.documents.dto.ApprovalRequestCreateRequest;
import com.team2.documents.dto.ApprovalRequestUpdateRequest;
import com.team2.documents.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.dto.ProformaInvoiceRegistrationResponse;
import com.team2.documents.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.command.service.ApprovalRequestCommandService;
import com.team2.documents.command.service.CollectionCommandService;
import com.team2.documents.command.service.ShipmentCommandService;
import com.team2.documents.command.service.ProformaInvoiceApprovalWorkflowService;
import com.team2.documents.command.service.ProformaInvoiceRejectionWorkflowService;
import com.team2.documents.command.service.ProformaInvoiceService;
import com.team2.documents.command.service.PurchaseOrderModificationService;
import com.team2.documents.command.service.PurchaseOrderModificationRequestService;
import com.team2.documents.command.service.PurchaseOrderDeletionRequestService;
import com.team2.documents.command.service.PurchaseOrderApprovalWorkflowService;
import com.team2.documents.command.service.PurchaseOrderCreationService;
import com.team2.documents.command.service.PurchaseOrderDocumentGenerationService;
import com.team2.documents.command.service.PurchaseOrderProductionOrderGenerationService;
import com.team2.documents.command.service.PurchaseOrderRejectionWorkflowService;
import com.team2.documents.command.service.PurchaseOrderRegistrationService;

@RestController
@RequestMapping("/api")
public class DocumentCommandController {

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
    private final ShipmentCommandService shipmentCommandService;
    private final CollectionCommandService collectionCommandService;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public DocumentCommandController(PurchaseOrderModificationService purchaseOrderModificationService,
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
        this.shipmentCommandService = shipmentCommandService;
        this.collectionCommandService = collectionCommandService;
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrderCreateResponse> create(@RequestBody PurchaseOrderCreateRequest request) {
        com.team2.documents.entity.PurchaseOrder purchaseOrder = purchaseOrderCreationService.create(request);
        return ResponseEntity.ok(new PurchaseOrderCreateResponse("PO 생성 요청이 처리되었습니다.", purchaseOrder.getPoId()));
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

    @PutMapping("/shipments/{shipmentId}")
    public ResponseEntity<com.team2.documents.entity.Shipment> updateShipmentStatus(@PathVariable Long shipmentId,
                                                                                     @RequestBody ShipmentStatusUpdateRequest request) {
        return ResponseEntity.ok(shipmentCommandService.updateStatus(shipmentId, request.status()));
    }

    @PutMapping("/collections/{collectionId}")
    public ResponseEntity<com.team2.documents.entity.Collection> updateCollection(@PathVariable Long collectionId,
                                                                                  @RequestBody CollectionUpdateRequest request) {
        return ResponseEntity.ok(collectionCommandService.complete(
                collectionId,
                request.status(),
                request.collectionCompletedDate()
        ));
    }

    @PostMapping("/approval-requests")
    public ResponseEntity<com.team2.documents.entity.ApprovalRequest> createApprovalRequest(
            @RequestBody ApprovalRequestCreateRequest request) {
        return ResponseEntity.ok(approvalRequestCommandService.create(request));
    }

    @PutMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<com.team2.documents.entity.ApprovalRequest> updateApprovalRequest(
            @PathVariable Long approvalRequestId,
            @RequestBody ApprovalRequestUpdateRequest request) {
        return ResponseEntity.ok(approvalRequestCommandService.update(approvalRequestId, request.status()));
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
