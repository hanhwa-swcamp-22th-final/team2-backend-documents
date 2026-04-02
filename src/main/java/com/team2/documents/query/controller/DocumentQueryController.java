package com.team2.documents.query.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.query.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.query.model.ApprovalRequestView;
import com.team2.documents.query.model.CollectionView;
import com.team2.documents.query.model.CommercialInvoiceView;
import com.team2.documents.query.model.PackingListView;
import com.team2.documents.query.model.ProformaInvoiceItemView;
import com.team2.documents.query.model.ProformaInvoiceView;
import com.team2.documents.query.model.ProductionOrderView;
import com.team2.documents.query.model.PurchaseOrderItemView;
import com.team2.documents.query.model.PurchaseOrderView;
import com.team2.documents.query.model.ShipmentOrderView;
import com.team2.documents.query.model.ShipmentView;
import com.team2.documents.query.service.ApprovalRequestQueryService;
import com.team2.documents.query.service.CommercialInvoiceQueryService;
import com.team2.documents.query.service.PackingListQueryService;
import com.team2.documents.query.service.DocsRevisionQueryService;
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
    private final DocsRevisionQueryService docsRevisionQueryService;

    public DocumentQueryController(PurchaseOrderQueryService purchaseOrderQueryService,
                                   ProformaInvoiceQueryService proformaInvoiceQueryService,
                                   CommercialInvoiceQueryService commercialInvoiceQueryService,
                                   PackingListQueryService packingListQueryService,
                                   ShipmentOrderQueryService shipmentOrderQueryService,
                                   ProductionOrderQueryService productionOrderQueryService,
                                   ShipmentQueryService shipmentQueryService,
                                   CollectionQueryService collectionQueryService,
                                   ApprovalRequestQueryService approvalRequestQueryService,
                                   DocsRevisionQueryService docsRevisionQueryService) {
        this.purchaseOrderQueryService = purchaseOrderQueryService;
        this.proformaInvoiceQueryService = proformaInvoiceQueryService;
        this.commercialInvoiceQueryService = commercialInvoiceQueryService;
        this.packingListQueryService = packingListQueryService;
        this.shipmentOrderQueryService = shipmentOrderQueryService;
        this.productionOrderQueryService = productionOrderQueryService;
        this.shipmentQueryService = shipmentQueryService;
        this.collectionQueryService = collectionQueryService;
        this.approvalRequestQueryService = approvalRequestQueryService;
        this.docsRevisionQueryService = docsRevisionQueryService;
    }

    @GetMapping("/proforma-invoices")
    public ResponseEntity<List<ProformaInvoiceResponse>> getProformaInvoices() {
        return ResponseEntity.ok(proformaInvoiceQueryService.findAll().stream().map(this::toProformaInvoiceResponse).toList());
    }

    @GetMapping("/proforma-invoices/{piId}")
    public ResponseEntity<ProformaInvoiceResponse> getProformaInvoice(@PathVariable String piId) {
        return ResponseEntity.ok(toProformaInvoiceResponse(proformaInvoiceQueryService.findById(piId)));
    }

    @GetMapping("/commercial-invoices")
    public ResponseEntity<List<CommercialInvoiceResponse>> getCommercialInvoices() {
        return ResponseEntity.ok(commercialInvoiceQueryService.findAll().stream().map(this::toCommercialInvoiceResponse).toList());
    }

    @GetMapping("/commercial-invoices/{ciId}")
    public ResponseEntity<CommercialInvoiceResponse> getCommercialInvoice(@PathVariable String ciId) {
        return ResponseEntity.ok(toCommercialInvoiceResponse(commercialInvoiceQueryService.findById(ciId)));
    }

    @GetMapping("/packing-lists")
    public ResponseEntity<List<PackingListResponse>> getPackingLists() {
        return ResponseEntity.ok(packingListQueryService.findAll().stream().map(this::toPackingListResponse).toList());
    }

    @GetMapping("/packing-lists/{plId}")
    public ResponseEntity<PackingListResponse> getPackingList(@PathVariable String plId) {
        return ResponseEntity.ok(toPackingListResponse(packingListQueryService.findById(plId)));
    }

    @GetMapping("/shipment-orders")
    public ResponseEntity<List<ShipmentOrderResponse>> getShipmentOrders() {
        return ResponseEntity.ok(shipmentOrderQueryService.findAll().stream().map(this::toShipmentOrderResponse).toList());
    }

    @GetMapping("/shipment-orders/{shipmentOrderId}")
    public ResponseEntity<ShipmentOrderResponse> getShipmentOrder(
            @PathVariable String shipmentOrderId) {
        return ResponseEntity.ok(toShipmentOrderResponse(shipmentOrderQueryService.findById(shipmentOrderId)));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrderResponse>> getPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderQueryService.findAll().stream().map(this::toPurchaseOrderResponse).toList());
    }

    @GetMapping("/purchase-orders/{poId}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrder(@PathVariable String poId) {
        return ResponseEntity.ok(toPurchaseOrderResponse(purchaseOrderQueryService.findById(poId)));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<PurchaseOrderInitialStatusResponse> determineInitialStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new PurchaseOrderInitialStatusResponse(
                purchaseOrderQueryService.determineInitialStatus(userId)));
    }

    @GetMapping("/approval-requests")
    public ResponseEntity<List<ApprovalRequestResponse>> getApprovalRequests() {
        return ResponseEntity.ok(approvalRequestQueryService.findAll().stream().map(this::toApprovalRequestResponse).toList());
    }

    @GetMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<ApprovalRequestResponse> getApprovalRequest(@PathVariable Long approvalRequestId) {
        return ResponseEntity.ok(toApprovalRequestResponse(approvalRequestQueryService.findById(approvalRequestId)));
    }

    @GetMapping("/approval-requests/document/{documentType}/{documentId}/status/{status}")
    public ResponseEntity<ApprovalRequestResponse> getApprovalRequestByDocumentAndStatus(
            @PathVariable String documentType,
            @PathVariable String documentId,
            @PathVariable String status) {
        return ResponseEntity.ok(
                toApprovalRequestResponse(approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status)));
    }

    @GetMapping("/production-orders")
    public ResponseEntity<List<ProductionOrderResponse>> getProductionOrders() {
        return ResponseEntity.ok(productionOrderQueryService.findAll().stream().map(this::toProductionOrderResponse).toList());
    }

    @GetMapping("/production-orders/{productionOrderId}")
    public ResponseEntity<ProductionOrderResponse> getProductionOrder(
            @PathVariable String productionOrderId) {
        return ResponseEntity.ok(toProductionOrderResponse(productionOrderQueryService.findById(productionOrderId)));
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentResponse>> getShipments() {
        return ResponseEntity.ok(shipmentQueryService.findAll().stream().map(this::toShipmentResponse).toList());
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(toShipmentResponse(shipmentQueryService.findById(shipmentId)));
    }

    @GetMapping("/collections")
    public ResponseEntity<List<CollectionResponse>> getCollections() {
        return ResponseEntity.ok(collectionQueryService.findAll().stream().map(this::toCollectionResponse).toList());
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<CollectionResponse> getCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(toCollectionResponse(collectionQueryService.findById(collectionId)));
    }

    private PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrderView purchaseOrder) {
        return new PurchaseOrderResponse(
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                purchaseOrder.getPiId(),
                purchaseOrder.getIssueDate(),
                purchaseOrder.getClientId(),
                purchaseOrder.getCurrencyId(),
                purchaseOrder.getManagerId(),
                purchaseOrder.getStatus(),
                purchaseOrder.getDeliveryDate(),
                purchaseOrder.getIncotermsCode(),
                purchaseOrder.getNamedPlace(),
                purchaseOrder.getSourceDeliveryDate(),
                purchaseOrder.isDeliveryDateOverride(),
                purchaseOrder.getTotalAmount(),
                purchaseOrder.getClientName(),
                purchaseOrder.getClientAddress(),
                purchaseOrder.getCountry(),
                purchaseOrder.getCurrencyCode(),
                purchaseOrder.getManagerName(),
                purchaseOrder.getApprovalStatus(),
                purchaseOrder.getRequestStatus(),
                purchaseOrder.getApprovalAction(),
                purchaseOrder.getApprovalRequestedBy(),
                purchaseOrder.getApprovalRequestedAt(),
                purchaseOrder.getApprovalReview(),
                purchaseOrder.getItemsSnapshot(),
                purchaseOrder.getLinkedDocuments(),
                docsRevisionQueryService.getRevisionHistory("PO", purchaseOrder.getPurchaseOrderId()),
                purchaseOrder.getCreatedAt(),
                purchaseOrder.getUpdatedAt(),
                purchaseOrder.getItems().stream().map(this::toPurchaseOrderItemResponse).toList()
        );
    }

    private PurchaseOrderItemResponse toPurchaseOrderItemResponse(PurchaseOrderItemView item) {
        return new PurchaseOrderItemResponse(
                item.getPoItemId(),
                item.getPoId(),
                item.getItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnit(),
                item.getUnitPrice(),
                item.getAmount(),
                item.getRemark()
        );
    }

    private ProformaInvoiceResponse toProformaInvoiceResponse(ProformaInvoiceView proformaInvoice) {
        return new ProformaInvoiceResponse(
                proformaInvoice.getPiId(),
                proformaInvoice.getStatus(),
                proformaInvoice.getIssueDate(),
                proformaInvoice.getClientId(),
                proformaInvoice.getCurrencyId(),
                proformaInvoice.getManagerId(),
                proformaInvoice.getDeliveryDate(),
                proformaInvoice.getIncotermsCode(),
                proformaInvoice.getNamedPlace(),
                proformaInvoice.getTotalAmount(),
                proformaInvoice.getClientName(),
                proformaInvoice.getClientAddress(),
                proformaInvoice.getCountry(),
                proformaInvoice.getCurrencyCode(),
                proformaInvoice.getManagerName(),
                proformaInvoice.getApprovalStatus(),
                proformaInvoice.getRequestStatus(),
                proformaInvoice.getApprovalAction(),
                proformaInvoice.getApprovalRequestedBy(),
                proformaInvoice.getApprovalRequestedAt(),
                proformaInvoice.getApprovalReview(),
                proformaInvoice.getItemsSnapshot(),
                proformaInvoice.getLinkedDocuments(),
                docsRevisionQueryService.getRevisionHistory("PI", proformaInvoice.getProformaInvoiceId()),
                proformaInvoice.getItems().stream().map(this::toProformaInvoiceItemResponse).toList()
        );
    }

    private ProformaInvoiceItemResponse toProformaInvoiceItemResponse(ProformaInvoiceItemView item) {
        return new ProformaInvoiceItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnit(),
                item.getUnitPrice(),
                item.getAmount(),
                item.getRemark()
        );
    }

    private CommercialInvoiceResponse toCommercialInvoiceResponse(CommercialInvoiceView commercialInvoice) {
        return new CommercialInvoiceResponse(
                commercialInvoice.getCommercialInvoiceId(),
                commercialInvoice.getCiId(),
                commercialInvoice.getPoId(),
                commercialInvoice.getInvoiceDate(),
                commercialInvoice.getClientId(),
                commercialInvoice.getCurrencyId(),
                commercialInvoice.getTotalAmount(),
                commercialInvoice.getStatus(),
                commercialInvoice.getCreatedAt()
        );
    }

    private PackingListResponse toPackingListResponse(PackingListView packingList) {
        return new PackingListResponse(
                packingList.getPackingListId(),
                packingList.getPlId(),
                packingList.getPoId(),
                packingList.getInvoiceDate(),
                packingList.getClientId(),
                packingList.getGrossWeight(),
                packingList.getStatus(),
                packingList.getCreatedAt()
        );
    }

    private ShipmentOrderResponse toShipmentOrderResponse(ShipmentOrderView shipmentOrder) {
        return new ShipmentOrderResponse(
                shipmentOrder.getShipmentOrderId(),
                shipmentOrder.getPoId(),
                shipmentOrder.getIssueDate(),
                shipmentOrder.getClientId(),
                shipmentOrder.getManagerId(),
                shipmentOrder.getStatus(),
                shipmentOrder.getDueDate(),
                shipmentOrder.getClientName(),
                shipmentOrder.getCountry(),
                shipmentOrder.getManagerName(),
                shipmentOrder.getItemName(),
                shipmentOrder.getLinkedDocuments(),
                shipmentOrder.getCreatedAt(),
                shipmentOrder.getUpdatedAt()
        );
    }

    private ApprovalRequestResponse toApprovalRequestResponse(ApprovalRequestView approvalRequest) {
        return new ApprovalRequestResponse(
                approvalRequest.getApprovalRequestId(),
                approvalRequest.getDocumentType(),
                approvalRequest.getDocumentId(),
                approvalRequest.getRequestType(),
                approvalRequest.getRequesterId(),
                approvalRequest.getApproverId(),
                approvalRequest.getComment(),
                approvalRequest.getReviewSnapshot(),
                approvalRequest.getRequestedAt(),
                approvalRequest.getReviewedAt(),
                approvalRequest.getStatus()
        );
    }

    private ProductionOrderResponse toProductionOrderResponse(ProductionOrderView productionOrder) {
        return new ProductionOrderResponse(
                productionOrder.getProductionOrderId(),
                productionOrder.getPoId(),
                productionOrder.getClientId(),
                productionOrder.getManagerId(),
                productionOrder.getPoNo(),
                productionOrder.getOrderDate(),
                productionOrder.getDueDate(),
                productionOrder.getStatus(),
                productionOrder.getClientName(),
                productionOrder.getCountry(),
                productionOrder.getManagerName(),
                productionOrder.getItemName(),
                productionOrder.getLinkedDocuments(),
                productionOrder.getItems(),
                productionOrder.getCreatedAt(),
                productionOrder.getUpdatedAt()
        );
    }

    private ShipmentResponse toShipmentResponse(ShipmentView shipment) {
        return new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getPoId(),
                shipment.getShipmentStatus()
        );
    }

    private CollectionResponse toCollectionResponse(CollectionView collection) {
        return new CollectionResponse(
                collection.getCollectionId(),
                collection.getPoId(),
                collection.getPoNo(),
                collection.getClientId(),
                collection.getClientName(),
                collection.getTotalAmount(),
                collection.getCollectedAmount(),
                collection.getRemainingAmount(),
                collection.getCurrencyCode(),
                collection.getStatus(),
                collection.getCollectionDate(),
                collection.getCreatedAt(),
                collection.getUpdatedAt()
        );
    }

    public record PurchaseOrderItemResponse(
            Long poItemId,
            Long poId,
            Integer itemId,
            String itemName,
            Integer quantity,
            String unit,
            BigDecimal unitPrice,
            BigDecimal amount,
            String remark
    ) {
    }

    public record PurchaseOrderResponse(
            Long purchaseOrderId,
            String poId,
            String piId,
            LocalDate issueDate,
            Integer clientId,
            Integer currencyId,
            Long managerId,
            String status,
            LocalDate deliveryDate,
            String incotermsCode,
            String namedPlace,
            LocalDate sourceDeliveryDate,
            boolean deliveryDateOverride,
            BigDecimal totalAmount,
            String clientName,
            String clientAddress,
            String country,
            String currencyCode,
            String managerName,
            String approvalStatus,
            String requestStatus,
            String approvalAction,
            String approvalRequestedBy,
            LocalDateTime approvalRequestedAt,
            String approvalReview,
            String itemsSnapshot,
            String linkedDocuments,
            String revisionHistory,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<PurchaseOrderItemResponse> items
    ) {
    }

    public record ProformaInvoiceItemResponse(
            Integer itemId,
            String itemName,
            Integer quantity,
            String unit,
            BigDecimal unitPrice,
            BigDecimal amount,
            String remark
    ) {
    }

    public record ProformaInvoiceResponse(
            String piId,
            String status,
            LocalDate issueDate,
            Integer clientId,
            Integer currencyId,
            Long managerId,
            LocalDate deliveryDate,
            String incotermsCode,
            String namedPlace,
            BigDecimal totalAmount,
            String clientName,
            String clientAddress,
            String country,
            String currencyCode,
            String managerName,
            String approvalStatus,
            String requestStatus,
            String approvalAction,
            String approvalRequestedBy,
            LocalDateTime approvalRequestedAt,
            String approvalReview,
            String itemsSnapshot,
            String linkedDocuments,
            String revisionHistory,
            List<ProformaInvoiceItemResponse> items
    ) {
    }

    public record CommercialInvoiceResponse(
            Long commercialInvoiceId,
            String ciId,
            Long poId,
            LocalDate invoiceDate,
            Integer clientId,
            Integer currencyId,
            BigDecimal totalAmount,
            String status,
            LocalDateTime createdAt
    ) {
    }

    public record PackingListResponse(
            Long packingListId,
            String plId,
            Long poId,
            LocalDate invoiceDate,
            Integer clientId,
            BigDecimal grossWeight,
            String status,
            LocalDateTime createdAt
    ) {
    }

    public record ShipmentOrderResponse(
            String shipmentOrderId,
            String poId,
            LocalDate issueDate,
            Integer clientId,
            Long managerId,
            String status,
            LocalDate dueDate,
            String clientName,
            String country,
            String managerName,
            String itemName,
            String linkedDocuments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record ApprovalRequestResponse(
            Long approvalRequestId,
            String documentType,
            String documentId,
            String requestType,
            Long requesterId,
            Long approverId,
            String comment,
            String reviewSnapshot,
            LocalDateTime requestedAt,
            LocalDateTime reviewedAt,
            String status
    ) {
    }

    public record ProductionOrderResponse(
            String productionOrderId,
            String poId,
            Integer clientId,
            Long managerId,
            String poNo,
            LocalDate orderDate,
            LocalDate dueDate,
            String status,
            String clientName,
            String country,
            String managerName,
            String itemName,
            String linkedDocuments,
            List<String> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record ShipmentResponse(
            Long shipmentId,
            String poId,
            String shipmentStatus
    ) {
    }

    public record CollectionResponse(
            Long collectionId,
            String poId,
            String poNo,
            Long clientId,
            String clientName,
            BigDecimal totalAmount,
            BigDecimal collectedAmount,
            BigDecimal remainingAmount,
            String currencyCode,
            String status,
            LocalDate collectionDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
