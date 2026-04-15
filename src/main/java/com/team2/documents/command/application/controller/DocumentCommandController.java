package com.team2.documents.command.application.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.team2.documents.command.application.dto.ProformaInvoiceCreateResponse;
import com.team2.documents.command.application.dto.ProformaInvoiceRegistrationResponse;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderCreateResponse;
import com.team2.documents.command.application.dto.PurchaseOrderDeletionRequest;
import com.team2.documents.command.application.dto.PurchaseOrderDeletionResponse;
import com.team2.documents.command.application.dto.PurchaseOrderModificationRequest;
import com.team2.documents.command.application.dto.PurchaseOrderModificationResponse;
import com.team2.documents.command.application.dto.PurchaseOrderRegistrationRequest;
import com.team2.documents.command.application.dto.PurchaseOrderRegistrationResponse;
import com.team2.documents.command.application.dto.CollectionUpdateRequest;
import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.application.dto.ApprovalRequestUpdateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.command.application.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.command.application.dto.EmailSendRequest;
import com.team2.documents.command.application.dto.EmailSendResponse;
import com.team2.documents.command.application.service.ApprovalRequestCommandService;
import com.team2.documents.command.application.service.CollectionCommandService;
import com.team2.documents.command.application.service.ProductionOrderCommandService;
import com.team2.documents.command.application.service.ShipmentCommandService;
import com.team2.documents.command.application.service.ProformaInvoiceApprovalWorkflowService;
import com.team2.documents.command.application.service.ProformaInvoiceCreationService;
import com.team2.documents.command.application.service.ProformaInvoiceRejectionWorkflowService;
import com.team2.documents.command.application.service.ProformaInvoiceService;
import com.team2.documents.command.application.service.PurchaseOrderModificationService;
import com.team2.documents.command.application.service.PurchaseOrderModificationRequestService;
import com.team2.documents.command.application.service.PurchaseOrderDeletionRequestService;
import com.team2.documents.command.application.service.PurchaseOrderApprovalWorkflowService;
import com.team2.documents.command.application.service.PurchaseOrderCreationService;
import com.team2.documents.command.application.service.PurchaseOrderDocumentGenerationService;
import com.team2.documents.command.application.service.PurchaseOrderProductionOrderGenerationService;
import com.team2.documents.command.application.service.PurchaseOrderRejectionWorkflowService;
import com.team2.documents.command.application.service.PurchaseOrderRegistrationService;
import com.team2.documents.command.application.service.EmailSendService;
import com.team2.documents.query.controller.DocumentQueryController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "문서 Command", description = "PI, PO, 출하, 수금, 결재 등 문서 생성/수정/삭제 명령 API")
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
    private final ProformaInvoiceCreationService proformaInvoiceCreationService;
    private final ProformaInvoiceService proformaInvoiceService;
    private final ShipmentCommandService shipmentCommandService;
    private final CollectionCommandService collectionCommandService;
    private final ApprovalRequestCommandService approvalRequestCommandService;
    private final EmailSendService emailSendService;
    private final ProductionOrderCommandService productionOrderCommandService;

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
                              ProformaInvoiceCreationService proformaInvoiceCreationService,
                              ProformaInvoiceService proformaInvoiceService,
                              ShipmentCommandService shipmentCommandService,
                              CollectionCommandService collectionCommandService,
                              ApprovalRequestCommandService approvalRequestCommandService,
                              EmailSendService emailSendService,
                              ProductionOrderCommandService productionOrderCommandService) {
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
        this.proformaInvoiceCreationService = proformaInvoiceCreationService;
        this.proformaInvoiceService = proformaInvoiceService;
        this.shipmentCommandService = shipmentCommandService;
        this.collectionCommandService = collectionCommandService;
        this.approvalRequestCommandService = approvalRequestCommandService;
        this.emailSendService = emailSendService;
        this.productionOrderCommandService = productionOrderCommandService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 생성", description = "새로운 발주서(PO)를 생성합니다. 품목 목록을 포함하여 요청할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/purchase-orders")
    public ResponseEntity<EntityModel<PurchaseOrderCreateResponse>> create(@RequestBody PurchaseOrderCreateRequest request) {
        com.team2.documents.command.domain.entity.PurchaseOrder purchaseOrder = purchaseOrderCreationService.create(request);
        PurchaseOrderCreateResponse response = new PurchaseOrderCreateResponse("PO 생성 요청이 처리되었습니다.", purchaseOrder.getPoId());
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(purchaseOrder.getPoId())).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders(0, 1000)).withRel("purchase-orders")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Proforma Invoice 생성", description = "새로운 견적송장(PI)을 생성합니다. 품목 목록을 포함하여 요청할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PI 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/proforma-invoices")
    public ResponseEntity<EntityModel<ProformaInvoiceCreateResponse>> createProformaInvoice(@RequestBody ProformaInvoiceCreateRequest request) {
        com.team2.documents.command.domain.entity.ProformaInvoice proformaInvoice = proformaInvoiceCreationService.create(request);
        ProformaInvoiceCreateResponse response = new ProformaInvoiceCreateResponse("PI 생성 요청이 처리되었습니다.", proformaInvoice.getPiId());
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(proformaInvoice.getPiId())).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoices(0, 1000)).withRel("proforma-invoices")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Proforma Invoice 등록 요청", description = "PI의 등록을 요청합니다. 결재 프로세스가 시작됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PI 등록 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "PI를 찾을 수 없음")
    })
    @PostMapping("/proforma-invoices/request-registration")
    public ResponseEntity<EntityModel<ProformaInvoiceRegistrationResponse>> requestRegistration(
            @RequestBody ProformaInvoiceRegistrationRequest request) {
        proformaInvoiceService.requestRegistration(request.piId(), request.userId());
        ProformaInvoiceRegistrationResponse response = new ProformaInvoiceRegistrationResponse("PI 등록 요청이 처리되었습니다.");
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(request.piId())).withRel("proforma-invoice")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 등록 요청", description = "PO의 등록을 요청합니다. 결재 프로세스가 시작됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 등록 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음")
    })
    @PostMapping("/purchase-orders/request-registration")
    public ResponseEntity<EntityModel<PurchaseOrderRegistrationResponse>> requestPurchaseOrderRegistration(
            @RequestBody PurchaseOrderRegistrationRequest request) {
        purchaseOrderRegistrationService.requestRegistration(request.poId(), request.userId());
        PurchaseOrderRegistrationResponse response = new PurchaseOrderRegistrationResponse("PO 등록 요청이 처리되었습니다.");
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(request.poId())).withRel("purchase-order")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 수정 요청", description = "PO의 수정을 요청합니다. 결재 프로세스가 시작됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 수정 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "수정 불가능한 상태")
    })
    @PostMapping("/purchase-orders/request-modification")
    public ResponseEntity<EntityModel<PurchaseOrderModificationResponse>> requestPurchaseOrderModification(
            @RequestBody PurchaseOrderModificationRequest request) {
        purchaseOrderModificationRequestService.requestModification(request.poId(), request.userId());
        PurchaseOrderModificationResponse response = new PurchaseOrderModificationResponse("PO 수정 요청이 처리되었습니다.");
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(request.poId())).withRel("purchase-order")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 삭제 요청", description = "PO의 삭제를 요청합니다. 결재 프로세스가 시작됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 삭제 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제 불가능한 상태")
    })
    @PostMapping("/purchase-orders/request-deletion")
    public ResponseEntity<EntityModel<PurchaseOrderDeletionResponse>> requestPurchaseOrderDeletion(
            @RequestBody PurchaseOrderDeletionRequest request) {
        purchaseOrderDeletionRequestService.requestDeletion(request.poId(), request.userId());
        PurchaseOrderDeletionResponse response = new PurchaseOrderDeletionResponse("PO 삭제 요청이 처리되었습니다.");
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders(0, 1000)).withRel("purchase-orders")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SHIPPING')")
    @Operation(summary = "출하 상태 변경", description = "출하(Shipment)의 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출하 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "출하를 찾을 수 없음")
    })
    @PutMapping("/shipments/{shipmentId}")
    public ResponseEntity<EntityModel<ShipmentResponse>> updateShipmentStatus(
            @Parameter(description = "출하 ID", example = "1") @PathVariable("shipmentId") Long shipmentId,
            @RequestBody ShipmentStatusUpdateRequest request) {
        ShipmentResponse response = toShipmentResponse(shipmentCommandService.updateStatus(shipmentId, request.status()));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getShipment(shipmentId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getShipments(0, 1000)).withRel("shipments")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "수금 완료 처리", description = "수금(Collection)의 상태를 완료로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수금 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "수금을 찾을 수 없음")
    })
    @PutMapping("/collections/{collectionId}")
    public ResponseEntity<EntityModel<CollectionResponse>> updateCollection(
            @Parameter(description = "수금 ID", example = "1") @PathVariable("collectionId") Long collectionId,
            @RequestBody CollectionUpdateRequest request) {
        CollectionResponse response = toCollectionResponse(collectionCommandService.complete(
                collectionId,
                request.status(),
                request.collectionCompletedDate()
        ));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getCollection(collectionId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getCollections(0, 1000)).withRel("collections")));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "결재 요청 생성", description = "새로운 결재 요청을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결재 요청 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/approval-requests")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> createApprovalRequest(
            @RequestBody ApprovalRequestCreateRequest request) {
        ApprovalRequestResponse response = toApprovalRequestResponse(approvalRequestCommandService.create(request));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(response.approvalRequestId())).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests(0, 1000)).withRel("approval-requests")));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "결재 요청 상태 변경", description = "결재 요청의 상태를 변경합니다 (승인/반려).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결재 요청 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "결재 요청을 찾을 수 없음")
    })
    @PutMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> updateApprovalRequest(
            @Parameter(description = "결재 요청 ID", example = "1") @PathVariable("approvalRequestId") Long approvalRequestId,
            @RequestBody ApprovalRequestUpdateRequest request) {
        ApprovalRequestResponse response = toApprovalRequestResponse(
                approvalRequestCommandService.update(approvalRequestId, request.status(), request.comment(), request.reason()));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(approvalRequestId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests(0, 1000)).withRel("approval-requests")));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "PO 수정 가능 여부 검증", description = "해당 PO가 수정 가능한 상태인지 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 가능"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "수정 불가능한 상태")
    })
    @PostMapping("/purchase-orders/{poId}/validate-modifiable")
    public ResponseEntity<Void> validateModifiable(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderModificationService.validateModifiable(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "PO 삭제 가능 여부 검증", description = "해당 PO가 삭제 가능한 상태인지 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 가능"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "삭제 불가능한 상태")
    })
    @PostMapping("/purchase-orders/{poId}/validate-deletable")
    public ResponseEntity<Void> validateDeletable(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderModificationService.validateDeletable(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "PO 관련 문서 자동 생성", description = "PO 확정 시 CI, PL, 선적지시서 등 관련 문서를 자동으로 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문서 생성 성공"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음")
    })
    @PostMapping("/purchase-orders/{poId}/generate-documents")
    public ResponseEntity<Void> generateDocuments(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderDocumentGenerationService.generateOnConfirmation(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','PRODUCTION')")
    @Operation(summary = "생산지시서 자동 생성", description = "PO 기반으로 생산지시서를 자동 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생산지시서 생성 성공"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음")
    })
    @PostMapping("/purchase-orders/{poId}/generate-production-order")
    public ResponseEntity<Void> generateProductionOrder(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderProductionOrderGenerationService.generate(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','PRODUCTION')")
    @Operation(summary = "생산완료 처리", description = "생산지시서 상태를 '생산완료'로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생산완료 처리 성공"),
            @ApiResponse(responseCode = "404", description = "생산지시서를 찾을 수 없음")
    })
    @PutMapping("/production-orders/{productionOrderId}/complete")
    public ResponseEntity<ProductionOrderCompleteResponse> completeProductionOrder(
            @Parameter(description = "생산지시서 ID", example = "PR-2026-0001")
            @PathVariable("productionOrderId") String productionOrderId) {
        var po = productionOrderCommandService.complete(productionOrderId);
        return ResponseEntity.ok(new ProductionOrderCompleteResponse(po.getProductionOrderId(), po.getStatus()));
    }

    @Schema(description = "생산완료 처리 응답")
    public record ProductionOrderCompleteResponse(
            @Schema(description = "생산지시서 ID") String productionOrderId,
            @Schema(description = "변경된 상태") String status
    ) {}

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 승인", description = "PO를 승인 처리합니다. 승인 시 관련 문서가 자동 생성될 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 승인 성공"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "승인 불가능한 상태")
    })
    @PostMapping("/purchase-orders/{poId}/approve")
    public ResponseEntity<Void> approvePurchaseOrder(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderApprovalWorkflowService.approve(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Purchase Order 반려", description = "PO를 반려 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PO 반려 성공"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "반려 불가능한 상태")
    })
    @PostMapping("/purchase-orders/{poId}/reject")
    public ResponseEntity<Void> rejectPurchaseOrder(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        purchaseOrderRejectionWorkflowService.reject(poId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Proforma Invoice 승인", description = "PI를 승인 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PI 승인 성공"),
            @ApiResponse(responseCode = "404", description = "PI를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "승인 불가능한 상태")
    })
    @PostMapping("/proforma-invoices/{piId}/approve")
    public ResponseEntity<Void> approveProformaInvoice(
            @Parameter(description = "PI 문서 ID", example = "PI-2026-0001") @PathVariable("piId") String piId) {
        proformaInvoiceApprovalWorkflowService.approve(piId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "Proforma Invoice 반려", description = "PI를 반려 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PI 반려 성공"),
            @ApiResponse(responseCode = "404", description = "PI를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "반려 불가능한 상태")
    })
    @PostMapping("/proforma-invoices/{piId}/reject")
    public ResponseEntity<Void> rejectProformaInvoice(
            @Parameter(description = "PI 문서 ID", example = "PI-2026-0001") @PathVariable("piId") String piId) {
        proformaInvoiceRejectionWorkflowService.reject(piId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @Operation(summary = "문서 첨부 이메일 발송", description = "선택한 문서 유형의 PDF를 생성하여 이메일로 발송합니다. Activity 서비스에 발송 이력을 기록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 발송 결과"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/emails/send")
    public ResponseEntity<EmailSendResponse> sendEmail(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EmailSendRequest request) {
        Long userId = Long.parseLong(jwt.getSubject());
        // 기본 경로 — Activity 에 이력 자동 기록
        EmailSendResponse response = emailSendService.sendEmail(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Activity 재전송 흐름 전용 내부 엔드포인트.
     *
     * <p>Activity 가 자기 EmailLog 의 상태를 직접 update 하므로,
     * Documents 는 실제 발송만 수행하고 로그 기록은 생략한다.
     * 이로써 resend 1회가 email_logs 테이블에 2개 row 를 만드는 중복 문제를 해결한다.
     *
     * <p>경로에 {@code /internal} 이 포함되어 있어:
     * <ul>
     *   <li>Gateway 에서 denyAll 로 외부 완전 차단</li>
     *   <li>Activity → Documents Feign 호출 시 X-Internal-Token 자동 주입 (InternalTokenFeignInterceptor)</li>
     *   <li>Documents 의 InternalApiTokenFilter (향후 추가) 또는 SecurityConfig permitAll 로 JWT 없이 수신</li>
     * </ul>
     *
     * <p>단, Documents 는 현재 InternalApiTokenFilter 수신 측 필터가 없으므로
     * 이 엔드포인트는 Bearer JWT 로도 호출 가능. Activity 가 Bearer 를 전파하지 않고
     * X-Internal-Token 만 보내기 때문에 추후 Documents 에도 InternalApiTokenFilter 추가 권장.
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "내부 전용: 로그 기록 없이 메일 발송",
               description = "Activity 재전송 흐름 전용. 이력 기록은 호출자(Activity)가 책임진다.")
    @PostMapping("/emails/internal/send-no-log")
    public ResponseEntity<EmailSendResponse> sendEmailWithoutLogging(
            @RequestHeader(name = "X-User-Id", required = false) Long headerUserId,
            @Valid @RequestBody EmailSendRequest request) {
        // 사용자 컨텍스트가 없는 시스템 호출이므로 헤더 기반 userId 또는 기본값(0) 사용.
        // Activity 가 원본 EmailLog 의 emailSenderId 를 X-User-Id 헤더로 전달.
        Long userId = headerUserId != null ? headerUserId : 0L;
        EmailSendResponse response = emailSendService.sendEmailWithoutLogging(userId, request);
        return ResponseEntity.ok(response);
    }

    private ShipmentResponse toShipmentResponse(com.team2.documents.command.domain.entity.Shipment shipment) {
        return new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getPoId(),
                shipment.getShipmentStatus().name()
        );
    }

    private CollectionResponse toCollectionResponse(com.team2.documents.command.domain.entity.Collection collection) {
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

    private ApprovalRequestResponse toApprovalRequestResponse(
            com.team2.documents.command.domain.entity.ApprovalRequest approvalRequest) {
        return new ApprovalRequestResponse(
                approvalRequest.getApprovalRequestId(),
                approvalRequest.getDocumentType().name(),
                approvalRequest.getDocumentId(),
                approvalRequest.getRequestType().name(),
                approvalRequest.getRequesterId(),
                approvalRequest.getApproverId(),
                approvalRequest.getComment(),
                approvalRequest.getReason(),
                approvalRequest.getReviewSnapshot(),
                approvalRequest.getRequestedAt(),
                approvalRequest.getReviewedAt(),
                approvalRequest.getStatus().name()
        );
    }

    @Schema(description = "출하 응답")
    public record ShipmentResponse(
            @Schema(description = "출하 ID") Long shipmentId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "출하 상태") String shipmentStatus
    ) {
    }

    @Schema(description = "수금 응답")
    public record CollectionResponse(
            @Schema(description = "수금 ID") Long collectionId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "PO 문서 번호") String poNo,
            @Schema(description = "거래처 ID") Long clientId,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "총 금액") BigDecimal totalAmount,
            @Schema(description = "수금 완료 금액") BigDecimal collectedAmount,
            @Schema(description = "미수금 금액") BigDecimal remainingAmount,
            @Schema(description = "통화 코드") String currencyCode,
            @Schema(description = "수금 상태") String status,
            @Schema(description = "수금일") LocalDate collectionDate,
            @Schema(description = "생성일시") LocalDateTime createdAt,
            @Schema(description = "수정일시") LocalDateTime updatedAt
    ) {
    }

    @Schema(description = "결재 요청 응답")
    public record ApprovalRequestResponse(
            @Schema(description = "결재 요청 ID") Long approvalRequestId,
            @Schema(description = "문서 유형") String documentType,
            @Schema(description = "문서 ID") String documentId,
            @Schema(description = "요청 유형") String requestType,
            @Schema(description = "요청자 ID") Long requesterId,
            @Schema(description = "결재자 ID") Long approverId,
            @Schema(description = "코멘트") String comment,
            @Schema(description = "반려 사유") String reason,
            @Schema(description = "검토 스냅샷") String reviewSnapshot,
            @Schema(description = "요청일시") LocalDateTime requestedAt,
            @Schema(description = "검토일시") LocalDateTime reviewedAt,
            @Schema(description = "결재 상태") String status
    ) {
    }
}
