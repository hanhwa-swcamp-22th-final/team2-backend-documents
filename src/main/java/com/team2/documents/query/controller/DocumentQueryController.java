package com.team2.documents.query.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
    public ResponseEntity<CollectionModel<EntityModel<ProformaInvoiceResponse>>> getProformaInvoices() {
        List<EntityModel<ProformaInvoiceResponse>> models = proformaInvoiceQueryService.findAll().stream()
                .map(this::toProformaInvoiceResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(r.piId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoices()).withSelfRel()));
    }

    @GetMapping("/proforma-invoices/{piId}")
    public ResponseEntity<EntityModel<ProformaInvoiceResponse>> getProformaInvoice(@PathVariable String piId) {
        ProformaInvoiceResponse response = toProformaInvoiceResponse(proformaInvoiceQueryService.findById(piId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(piId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoices()).withRel("proforma-invoices")));
    }

    @GetMapping("/commercial-invoices")
    public ResponseEntity<CollectionModel<EntityModel<CommercialInvoiceResponse>>> getCommercialInvoices() {
        List<EntityModel<CommercialInvoiceResponse>> models = commercialInvoiceQueryService.findAll().stream()
                .map(this::toCommercialInvoiceResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getCommercialInvoice(r.ciId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoices()).withSelfRel()));
    }

    @GetMapping("/commercial-invoices/{ciId}")
    public ResponseEntity<EntityModel<CommercialInvoiceResponse>> getCommercialInvoice(@PathVariable String ciId) {
        CommercialInvoiceResponse response = toCommercialInvoiceResponse(commercialInvoiceQueryService.findById(ciId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoice(ciId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoices()).withRel("commercial-invoices")));
    }

    @GetMapping("/packing-lists")
    public ResponseEntity<CollectionModel<EntityModel<PackingListResponse>>> getPackingLists() {
        List<EntityModel<PackingListResponse>> models = packingListQueryService.findAll().stream()
                .map(this::toPackingListResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getPackingList(r.plId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getPackingLists()).withSelfRel()));
    }

    @GetMapping("/packing-lists/{plId}")
    public ResponseEntity<EntityModel<PackingListResponse>> getPackingList(@PathVariable String plId) {
        PackingListResponse response = toPackingListResponse(packingListQueryService.findById(plId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPackingList(plId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getPackingLists()).withRel("packing-lists")));
    }

    @GetMapping("/shipment-orders")
    public ResponseEntity<CollectionModel<EntityModel<ShipmentOrderResponse>>> getShipmentOrders() {
        List<EntityModel<ShipmentOrderResponse>> models = shipmentOrderQueryService.findAll().stream()
                .map(this::toShipmentOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getShipmentOrder(r.shipmentOrderId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrders()).withSelfRel()));
    }

    @GetMapping("/shipment-orders/{shipmentOrderId}")
    public ResponseEntity<EntityModel<ShipmentOrderResponse>> getShipmentOrder(
            @PathVariable String shipmentOrderId) {
        ShipmentOrderResponse response = toShipmentOrderResponse(shipmentOrderQueryService.findById(shipmentOrderId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrder(shipmentOrderId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrders()).withRel("shipment-orders")));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<CollectionModel<EntityModel<PurchaseOrderResponse>>> getPurchaseOrders() {
        List<EntityModel<PurchaseOrderResponse>> models = purchaseOrderQueryService.findAll().stream()
                .map(this::toPurchaseOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(r.poId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders()).withSelfRel()));
    }

    @GetMapping("/purchase-orders/{poId}")
    public ResponseEntity<EntityModel<PurchaseOrderResponse>> getPurchaseOrder(@PathVariable String poId) {
        PurchaseOrderResponse response = toPurchaseOrderResponse(purchaseOrderQueryService.findById(poId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(poId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders()).withRel("purchase-orders")));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<EntityModel<PurchaseOrderInitialStatusResponse>> determineInitialStatus(@PathVariable Long userId) {
        PurchaseOrderInitialStatusResponse response = new PurchaseOrderInitialStatusResponse(
                purchaseOrderQueryService.determineInitialStatus(userId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).determineInitialStatus(userId)).withSelfRel()));
    }

    @GetMapping("/approval-requests")
    public ResponseEntity<CollectionModel<EntityModel<ApprovalRequestResponse>>> getApprovalRequests() {
        List<EntityModel<ApprovalRequestResponse>> models = approvalRequestQueryService.findAll().stream()
                .map(this::toApprovalRequestResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(r.approvalRequestId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests()).withSelfRel()));
    }

    @GetMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> getApprovalRequest(@PathVariable Long approvalRequestId) {
        ApprovalRequestResponse response = toApprovalRequestResponse(approvalRequestQueryService.findById(approvalRequestId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(approvalRequestId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests()).withRel("approval-requests")));
    }

    @GetMapping("/approval-requests/document/{documentType}/{documentId}/status/{status}")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> getApprovalRequestByDocumentAndStatus(
            @PathVariable String documentType,
            @PathVariable String documentId,
            @PathVariable String status) {
        ApprovalRequestResponse response = toApprovalRequestResponse(
                approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequestByDocumentAndStatus(documentType, documentId, status)).withSelfRel()));
    }

    @GetMapping("/production-orders")
    public ResponseEntity<CollectionModel<EntityModel<ProductionOrderResponse>>> getProductionOrders() {
        List<EntityModel<ProductionOrderResponse>> models = productionOrderQueryService.findAll().stream()
                .map(this::toProductionOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getProductionOrder(r.productionOrderId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getProductionOrders()).withSelfRel()));
    }

    @GetMapping("/production-orders/{productionOrderId}")
    public ResponseEntity<EntityModel<ProductionOrderResponse>> getProductionOrder(
            @PathVariable String productionOrderId) {
        ProductionOrderResponse response = toProductionOrderResponse(productionOrderQueryService.findById(productionOrderId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProductionOrder(productionOrderId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getProductionOrders()).withRel("production-orders")));
    }

    @GetMapping("/shipments")
    public ResponseEntity<CollectionModel<EntityModel<ShipmentResponse>>> getShipments() {
        List<EntityModel<ShipmentResponse>> models = shipmentQueryService.findAll().stream()
                .map(this::toShipmentResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getShipment(r.shipmentId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getShipments()).withSelfRel()));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<EntityModel<ShipmentResponse>> getShipment(@PathVariable Long shipmentId) {
        ShipmentResponse response = toShipmentResponse(shipmentQueryService.findById(shipmentId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getShipment(shipmentId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getShipments()).withRel("shipments")));
    }

    @GetMapping("/collections")
    public ResponseEntity<CollectionModel<EntityModel<CollectionResponse>>> getCollections() {
        List<EntityModel<CollectionResponse>> models = collectionQueryService.findAll().stream()
                .map(this::toCollectionResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getCollection(r.collectionId())).withSelfRel()))
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(DocumentQueryController.class).getCollections()).withSelfRel()));
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<EntityModel<CollectionResponse>> getCollection(@PathVariable Long collectionId) {
        CollectionResponse response = toCollectionResponse(collectionQueryService.findById(collectionId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getCollection(collectionId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getCollections()).withRel("collections")));
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
