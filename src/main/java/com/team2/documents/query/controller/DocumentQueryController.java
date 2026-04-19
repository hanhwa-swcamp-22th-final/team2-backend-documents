package com.team2.documents.query.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.command.application.service.UserSnapshotService;
import com.team2.documents.infrastructure.pdf.PdfGenerationService;
import com.team2.documents.query.dto.PagedResult;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "문서 Query", description = "PI, PO, CI, PL, 선적지시서, 생산지시서, 출하, 수금, 결재 조회 API")
@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
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
    private final PdfGenerationService pdfGenerationService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;
    private final PackingListJpaRepository packingListJpaRepository;
    private final ShipmentOrderJpaRepository shipmentOrderJpaRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final UserSnapshotService userSnapshotService;
    private final com.team2.documents.command.infrastructure.client.AuthFeignClient authFeignClient;

    public DocumentQueryController(PurchaseOrderQueryService purchaseOrderQueryService,
                                   ProformaInvoiceQueryService proformaInvoiceQueryService,
                                   CommercialInvoiceQueryService commercialInvoiceQueryService,
                                   PackingListQueryService packingListQueryService,
                                   ShipmentOrderQueryService shipmentOrderQueryService,
                                   ProductionOrderQueryService productionOrderQueryService,
                                   ShipmentQueryService shipmentQueryService,
                                   CollectionQueryService collectionQueryService,
                                   ApprovalRequestQueryService approvalRequestQueryService,
                                   DocsRevisionQueryService docsRevisionQueryService,
                                   PdfGenerationService pdfGenerationService,
                                   PurchaseOrderRepository purchaseOrderRepository,
                                   ProformaInvoiceRepository proformaInvoiceRepository,
                                   CommercialInvoiceJpaRepository commercialInvoiceJpaRepository,
                                   PackingListJpaRepository packingListJpaRepository,
                                   ShipmentOrderJpaRepository shipmentOrderJpaRepository,
                                   ProductionOrderRepository productionOrderRepository,
                                   UserSnapshotService userSnapshotService,
                                   com.team2.documents.command.infrastructure.client.AuthFeignClient authFeignClient) {
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
        this.pdfGenerationService = pdfGenerationService;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.proformaInvoiceRepository = proformaInvoiceRepository;
        this.commercialInvoiceJpaRepository = commercialInvoiceJpaRepository;
        this.packingListJpaRepository = packingListJpaRepository;
        this.shipmentOrderJpaRepository = shipmentOrderJpaRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.userSnapshotService = userSnapshotService;
        this.authFeignClient = authFeignClient;
    }

    /**
     * 현재 요청자의 managerId 스코프를 계산.
     * - ADMIN → null (전체 조회)
     * - 그 외 → JWT teamId 의 팀원 user_id 리스트. 팀 미소속 / Feign 실패 시 빈 리스트 (결과 0건).
     */
    private List<Long> resolveManagerIdScope() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return List.of();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) return null;
        Integer teamId = null;
        if (auth.getPrincipal() instanceof Jwt jwt) {
            Object claim = jwt.getClaim("teamId");
            if (claim instanceof Number num) teamId = num.intValue();
        }
        if (teamId == null) return List.of();
        try {
            List<Long> ids = authFeignClient.getTeamMemberIds(teamId);
            return ids != null ? ids : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private void assertInScope(Long managerId) {
        List<Long> scope = resolveManagerIdScope();
        if (scope == null) return;
        if (managerId == null || !scope.contains(managerId)) {
            throw new AccessDeniedException("해당 문서에 대한 접근 권한이 없습니다.");
        }
    }

    @Operation(summary = "결재자 후보 조회",
            description = "요청자 팀의 팀장(position_level=1) + ADMIN 사용자 목록. teamId 미지정 시 전 팀의 팀장을 반환.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/approval-requests/approvers")
    public ResponseEntity<List<com.team2.documents.command.infrastructure.client.AuthInternalUserResponse>> getApprovers(
            @Parameter(description = "요청자 팀 ID (미지정 시 전체 팀장)")
            @RequestParam(name = "teamId", required = false) Integer teamId) {
        return ResponseEntity.ok(authFeignClient.getApprovers(teamId));
    }

    @Operation(summary = "Proforma Invoice 전체 조회", description = "ADMIN 전체, 그 외 JWT teamId 기준 팀원 작성 PI 만 반환.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/proforma-invoices")
    public ResponseEntity<PagedModel<EntityModel<ProformaInvoiceResponse>>> getProformaInvoices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        List<Long> scope = resolveManagerIdScope();
        PagedResult<ProformaInvoiceView> result = proformaInvoiceQueryService.findAll(page, size, scope);
        List<EntityModel<ProformaInvoiceResponse>> models = result.content().stream()
                .map(this::toProformaInvoiceResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(r.piId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoices(page, size)).withSelfRel()));
    }

    @Operation(summary = "Proforma Invoice 단건 조회", description = "PI ID로 견적송장을 조회합니다. 팀 스코프 위반 시 403.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "팀 스코프 위반"),
            @ApiResponse(responseCode = "404", description = "PI를 찾을 수 없음")
    })
    @GetMapping("/proforma-invoices/{piId}")
    public ResponseEntity<EntityModel<ProformaInvoiceResponse>> getProformaInvoice(
            @Parameter(description = "PI 문서 ID", example = "PI-2026-0001") @PathVariable("piId") String piId) {
        ProformaInvoiceView view = proformaInvoiceQueryService.findById(piId);
        assertInScope(view.getManagerId());
        ProformaInvoiceResponse response = toProformaInvoiceResponse(view);
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoice(piId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getProformaInvoices(0, 1000)).withRel("proforma-invoices")));
    }

    @Operation(summary = "Commercial Invoice 전체 조회", description = "모든 상업송장(CI) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/commercial-invoices")
    public ResponseEntity<PagedModel<EntityModel<CommercialInvoiceResponse>>> getCommercialInvoices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<CommercialInvoiceView> result = commercialInvoiceQueryService.findAll(page, size);
        List<EntityModel<CommercialInvoiceResponse>> models = result.content().stream()
                .map(this::toCommercialInvoiceResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getCommercialInvoice(r.ciId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoices(page, size)).withSelfRel()));
    }

    @Operation(summary = "Commercial Invoice 단건 조회", description = "CI ID로 상업송장을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "CI를 찾을 수 없음")
    })
    @GetMapping("/commercial-invoices/{ciId}")
    public ResponseEntity<EntityModel<CommercialInvoiceResponse>> getCommercialInvoice(
            @Parameter(description = "CI 문서 ID", example = "CI-2026-0001") @PathVariable("ciId") String ciId) {
        CommercialInvoiceResponse response = toCommercialInvoiceResponse(commercialInvoiceQueryService.findById(ciId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoice(ciId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getCommercialInvoices(0, 1000)).withRel("commercial-invoices")));
    }

    @Operation(summary = "Packing List 전체 조회", description = "모든 패킹리스트(PL) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/packing-lists")
    public ResponseEntity<PagedModel<EntityModel<PackingListResponse>>> getPackingLists(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<PackingListView> result = packingListQueryService.findAll(page, size);
        List<EntityModel<PackingListResponse>> models = result.content().stream()
                .map(this::toPackingListResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getPackingList(r.plId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getPackingLists(page, size)).withSelfRel()));
    }

    @Operation(summary = "Packing List 단건 조회", description = "PL ID로 패킹리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "PL을 찾을 수 없음")
    })
    @GetMapping("/packing-lists/{plId}")
    public ResponseEntity<EntityModel<PackingListResponse>> getPackingList(
            @Parameter(description = "PL 문서 ID", example = "PL-2026-0001") @PathVariable("plId") String plId) {
        PackingListResponse response = toPackingListResponse(packingListQueryService.findById(plId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPackingList(plId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getPackingLists(0, 1000)).withRel("packing-lists")));
    }

    @Operation(summary = "선적지시서 전체 조회", description = "모든 선적지시서(Shipment Order) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/shipment-orders")
    public ResponseEntity<PagedModel<EntityModel<ShipmentOrderResponse>>> getShipmentOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<ShipmentOrderView> result = shipmentOrderQueryService.findAll(page, size);
        List<EntityModel<ShipmentOrderResponse>> models = result.content().stream()
                .map(this::toShipmentOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getShipmentOrder(r.shipmentOrderId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrders(page, size)).withSelfRel()));
    }

    @Operation(summary = "선적지시서 단건 조회", description = "선적지시서 ID로 선적지시서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "선적지시서를 찾을 수 없음")
    })
    @GetMapping("/shipment-orders/{shipmentOrderId}")
    public ResponseEntity<EntityModel<ShipmentOrderResponse>> getShipmentOrder(
            @Parameter(description = "선적지시서 ID", example = "SO-2026-0001") @PathVariable("shipmentOrderId") String shipmentOrderId) {
        ShipmentOrderResponse response = toShipmentOrderResponse(shipmentOrderQueryService.findById(shipmentOrderId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrder(shipmentOrderId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getShipmentOrders(0, 1000)).withRel("shipment-orders")));
    }

    @Operation(summary = "Purchase Order 전체 조회", description = "모든 발주서(PO) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/purchase-orders")
    public ResponseEntity<PagedModel<EntityModel<PurchaseOrderResponse>>> getPurchaseOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        List<Long> scope = resolveManagerIdScope();
        PagedResult<PurchaseOrderView> result = purchaseOrderQueryService.findAll(page, size, scope);
        List<EntityModel<PurchaseOrderResponse>> models = result.content().stream()
                .map(this::toPurchaseOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(r.poId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders(page, size)).withSelfRel()));
    }

    @Operation(summary = "Purchase Order 단건 조회", description = "PO ID로 발주서를 조회합니다. 팀 스코프 위반 시 403.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "팀 스코프 위반"),
            @ApiResponse(responseCode = "404", description = "PO를 찾을 수 없음")
    })
    @GetMapping("/purchase-orders/{poId}")
    public ResponseEntity<EntityModel<PurchaseOrderResponse>> getPurchaseOrder(
            @Parameter(description = "PO 문서 ID", example = "PO-2026-0001") @PathVariable("poId") String poId) {
        PurchaseOrderView view = purchaseOrderQueryService.findById(poId);
        assertInScope(view.getManagerId());
        PurchaseOrderResponse response = toPurchaseOrderResponse(view);
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrder(poId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getPurchaseOrders(0, 1000)).withRel("purchase-orders")));
    }

    @Operation(summary = "PO 초기 상태 조회", description = "사용자 직급에 따라 PO 생성 시 초기 상태를 결정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<EntityModel<PurchaseOrderInitialStatusResponse>> determineInitialStatus(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable("userId") Long userId) {
        PurchaseOrderInitialStatusResponse response = new PurchaseOrderInitialStatusResponse(
                purchaseOrderQueryService.determineInitialStatus(userId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).determineInitialStatus(userId)).withSelfRel()));
    }

    @Operation(summary = "결재 요청 전체 조회", description = "모든 결재 요청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/approval-requests")
    public ResponseEntity<PagedModel<EntityModel<ApprovalRequestResponse>>> getApprovalRequests(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<ApprovalRequestView> result = approvalRequestQueryService.findAll(page, size);
        List<EntityModel<ApprovalRequestResponse>> models = result.content().stream()
                .map(this::toApprovalRequestResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(r.approvalRequestId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests(page, size)).withSelfRel()));
    }

    @Operation(summary = "결재 요청 단건 조회", description = "결재 요청 ID로 결재 요청을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "결재 요청을 찾을 수 없음")
    })
    @GetMapping("/approval-requests/{approvalRequestId}")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> getApprovalRequest(
            @Parameter(description = "결재 요청 ID", example = "1") @PathVariable("approvalRequestId") Long approvalRequestId) {
        ApprovalRequestResponse response = toApprovalRequestResponse(approvalRequestQueryService.findById(approvalRequestId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequest(approvalRequestId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequests(0, 1000)).withRel("approval-requests")));
    }

    @Operation(summary = "문서 유형/ID/상태별 결재 요청 조회", description = "문서 유형, 문서 ID, 결재 상태로 결재 요청을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "결재 요청을 찾을 수 없음")
    })
    @GetMapping("/approval-requests/document/{documentType}/{documentId}/status/{status}")
    public ResponseEntity<EntityModel<ApprovalRequestResponse>> getApprovalRequestByDocumentAndStatus(
            @Parameter(description = "문서 유형 (PI, PO 등)", example = "PO") @PathVariable("documentType") String documentType,
            @Parameter(description = "문서 ID", example = "PO-2026-0001") @PathVariable("documentId") String documentId,
            @Parameter(description = "결재 상태", example = "PENDING") @PathVariable("status") String status) {
        ApprovalRequestResponse response = toApprovalRequestResponse(
                approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getApprovalRequestByDocumentAndStatus(documentType, documentId, status)).withSelfRel()));
    }

    @Operation(summary = "생산지시서 전체 조회", description = "모든 생산지시서(Production Order) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/production-orders")
    public ResponseEntity<PagedModel<EntityModel<ProductionOrderResponse>>> getProductionOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<ProductionOrderView> result = productionOrderQueryService.findAll(page, size);
        List<EntityModel<ProductionOrderResponse>> models = result.content().stream()
                .map(this::toProductionOrderResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getProductionOrder(r.productionOrderId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getProductionOrders(page, size)).withSelfRel()));
    }

    @Operation(summary = "생산지시서 단건 조회", description = "생산지시서 ID로 생산지시서를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "생산지시서를 찾을 수 없음")
    })
    @GetMapping("/production-orders/{productionOrderId}")
    public ResponseEntity<EntityModel<ProductionOrderResponse>> getProductionOrder(
            @Parameter(description = "생산지시서 ID", example = "PRD-2026-0001") @PathVariable("productionOrderId") String productionOrderId) {
        ProductionOrderResponse response = toProductionOrderResponse(productionOrderQueryService.findById(productionOrderId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getProductionOrder(productionOrderId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getProductionOrders(0, 1000)).withRel("production-orders")));
    }

    @Operation(summary = "출하 전체 조회", description = "모든 출하(Shipment) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/shipments")
    public ResponseEntity<PagedModel<EntityModel<ShipmentResponse>>> getShipments(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<ShipmentView> result = shipmentQueryService.findAll(page, size);
        List<EntityModel<ShipmentResponse>> models = result.content().stream()
                .map(this::toShipmentResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getShipment(r.shipmentId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getShipments(page, size)).withSelfRel()));
    }

    @Operation(summary = "출하 단건 조회", description = "출하 ID로 출하를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "출하를 찾을 수 없음")
    })
    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<EntityModel<ShipmentResponse>> getShipment(
            @Parameter(description = "출하 ID", example = "1") @PathVariable("shipmentId") Long shipmentId) {
        ShipmentResponse response = toShipmentResponse(shipmentQueryService.findById(shipmentId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getShipment(shipmentId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getShipments(0, 1000)).withRel("shipments")));
    }

    @Operation(summary = "수금 전체 조회", description = "모든 수금(Collection) 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/collections")
    public ResponseEntity<PagedModel<EntityModel<CollectionResponse>>> getCollections(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "1000") int size) {
        PagedResult<CollectionView> result = collectionQueryService.findAll(page, size);
        List<EntityModel<CollectionResponse>> models = result.content().stream()
                .map(this::toCollectionResponse)
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(DocumentQueryController.class).getCollection(r.collectionId())).withSelfRel()))
                .toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(size, page, result.totalElements());
        return ResponseEntity.ok(PagedModel.of(models, metadata,
                linkTo(methodOn(DocumentQueryController.class).getCollections(page, size)).withSelfRel()));
    }

    @Operation(summary = "수금 단건 조회", description = "수금 ID로 수금을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "수금을 찾을 수 없음")
    })
    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<EntityModel<CollectionResponse>> getCollection(
            @Parameter(description = "수금 ID", example = "1") @PathVariable("collectionId") Long collectionId) {
        CollectionResponse response = toCollectionResponse(collectionQueryService.findById(collectionId));
        return ResponseEntity.ok(EntityModel.of(response,
                linkTo(methodOn(DocumentQueryController.class).getCollection(collectionId)).withSelfRel(),
                linkTo(methodOn(DocumentQueryController.class).getCollections(0, 1000)).withRel("collections")));
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
                purchaseOrder.getRemarks(),
                docsRevisionQueryService.getRevisionHistory("PO", purchaseOrder.getPurchaseOrderId()),
                purchaseOrder.getShipmentStatus(),
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
                proformaInvoice.getRemarks(),
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

    private CommercialInvoiceResponse toCommercialInvoiceResponse(CommercialInvoiceView ci) {
        return new CommercialInvoiceResponse(
                ci.getCommercialInvoiceId(),
                ci.getCiId(),
                ci.getPoId(),
                ci.getInvoiceDate(),
                ci.getClientId(),
                ci.getCurrencyId(),
                ci.getTotalAmount(),
                ci.getStatus(),
                ci.getClientName(),
                ci.getClientAddress(),
                ci.getCountry(),
                ci.getCurrencyCode(),
                ci.getPaymentTerms(),
                ci.getPortOfDischarge(),
                ci.getBuyer(),
                ci.getItemsSnapshot(),
                ci.getLinkedDocuments(),
                ci.getCreatedAt()
        );
    }

    private PackingListResponse toPackingListResponse(PackingListView pl) {
        return new PackingListResponse(
                pl.getPackingListId(),
                pl.getPlId(),
                pl.getPoId(),
                pl.getInvoiceDate(),
                pl.getClientId(),
                pl.getGrossWeight(),
                pl.getStatus(),
                pl.getClientName(),
                pl.getClientAddress(),
                pl.getCountry(),
                pl.getPaymentTerms(),
                pl.getPortOfDischarge(),
                pl.getBuyer(),
                pl.getItemsSnapshot(),
                pl.getLinkedDocuments(),
                pl.getCreatedAt()
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
                resolveUserName(approvalRequest.getRequesterId()),
                approvalRequest.getApproverId(),
                resolveUserName(approvalRequest.getApproverId()),
                approvalRequest.getComment(),
                approvalRequest.getReason(),
                approvalRequest.getReviewSnapshot(),
                approvalRequest.getRequestedAt(),
                approvalRequest.getReviewedAt(),
                approvalRequest.getStatus()
        );
    }

    /** Auth 서비스에서 사용자명 best-effort 조회. id null → null, Feign 실패 → null. */
    private String resolveUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        String name = userSnapshotService.resolveRequesterDisplayName(userId);
        return (name == null || name.isBlank() || name.equals(String.valueOf(userId))) ? null : name;
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
                productionOrder.getItemsSnapshot(),
                productionOrder.getCreatedAt(),
                productionOrder.getUpdatedAt()
        );
    }

    private ShipmentResponse toShipmentResponse(ShipmentView shipment) {
        return new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getPoId(),
                shipment.getShipmentOrderId(),
                shipment.getShipmentStatus(),
                shipment.getClientName(),
                shipment.getCountry(),
                shipment.getManagerName(),
                shipment.getItemName(),
                shipment.getRequestDate(),
                shipment.getDueDate()
        );
    }

    private CollectionResponse toCollectionResponse(CollectionView collection) {
        return new CollectionResponse(
                collection.getCollectionId(),
                collection.getPoId(),
                collection.getPoNo(),
                collection.getClientId(),
                collection.getClientName(),
                collection.getCountry(),
                collection.getManagerName(),
                collection.getTotalAmount(),
                collection.getCollectedAmount(),
                collection.getRemainingAmount(),
                collection.getCurrencyCode(),
                collection.getStatus(),
                collection.getIssueDate(),
                collection.getCollectionDate(),
                collection.getCreatedAt(),
                collection.getUpdatedAt()
        );
    }

    @Schema(description = "Purchase Order 품목 응답")
    public record PurchaseOrderItemResponse(
            @Schema(description = "PO 품목 ID") Long poItemId,
            @Schema(description = "PO 내부 ID") Long poId,
            @Schema(description = "품목 ID") Integer itemId,
            @Schema(description = "품목명") String itemName,
            @Schema(description = "수량") Integer quantity,
            @Schema(description = "단위") String unit,
            @Schema(description = "단가") BigDecimal unitPrice,
            @Schema(description = "금액") BigDecimal amount,
            @Schema(description = "비고") String remark
    ) {
    }

    @Schema(description = "Purchase Order 응답")
    public record PurchaseOrderResponse(
            @Schema(description = "PO 내부 ID") Long purchaseOrderId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "연결된 PI 문서 ID") String piId,
            @Schema(description = "발행일") LocalDate issueDate,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "통화 ID") Integer currencyId,
            @Schema(description = "담당자 ID") Long managerId,
            @Schema(description = "PO 상태") String status,
            @Schema(description = "납기일") LocalDate deliveryDate,
            @Schema(description = "인코텀즈 코드") String incotermsCode,
            @Schema(description = "지정 장소") String namedPlace,
            @Schema(description = "원본 납기일") LocalDate sourceDeliveryDate,
            @Schema(description = "납기일 수동 변경 여부") boolean deliveryDateOverride,
            @Schema(description = "총 금액") BigDecimal totalAmount,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "거래처 주소") String clientAddress,
            @Schema(description = "국가") String country,
            @Schema(description = "통화 코드") String currencyCode,
            @Schema(description = "담당자명") String managerName,
            @Schema(description = "결재 상태") String approvalStatus,
            @Schema(description = "요청 상태") String requestStatus,
            @Schema(description = "결재 액션") String approvalAction,
            @Schema(description = "결재 요청자") String approvalRequestedBy,
            @Schema(description = "결재 요청일시") LocalDateTime approvalRequestedAt,
            @Schema(description = "결재 검토 내용") String approvalReview,
            @Schema(description = "품목 스냅샷 (JSON)") String itemsSnapshot,
            @Schema(description = "연결 문서 (JSON)") String linkedDocuments,
            @Schema(description = "특기사항") String remarks,
            @Schema(description = "수정 이력") String revisionHistory,
            @Schema(description = "출하 진행 상태 (null=출하전 / preparing / completed)") String shipmentStatus,
            @Schema(description = "생성일시") LocalDateTime createdAt,
            @Schema(description = "수정일시") LocalDateTime updatedAt,
            @Schema(description = "PO 품목 목록") List<PurchaseOrderItemResponse> items
    ) {
    }

    @Schema(description = "Proforma Invoice 품목 응답")
    public record ProformaInvoiceItemResponse(
            @Schema(description = "품목 ID") Integer itemId,
            @Schema(description = "품목명") String itemName,
            @Schema(description = "수량") Integer quantity,
            @Schema(description = "단위") String unit,
            @Schema(description = "단가") BigDecimal unitPrice,
            @Schema(description = "금액") BigDecimal amount,
            @Schema(description = "비고") String remark
    ) {
    }

    @Schema(description = "Proforma Invoice 응답")
    public record ProformaInvoiceResponse(
            @Schema(description = "PI 문서 ID") String piId,
            @Schema(description = "PI 상태") String status,
            @Schema(description = "발행일") LocalDate issueDate,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "통화 ID") Integer currencyId,
            @Schema(description = "담당자 ID") Long managerId,
            @Schema(description = "납기일") LocalDate deliveryDate,
            @Schema(description = "인코텀즈 코드") String incotermsCode,
            @Schema(description = "지정 장소") String namedPlace,
            @Schema(description = "총 금액") BigDecimal totalAmount,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "거래처 주소") String clientAddress,
            @Schema(description = "국가") String country,
            @Schema(description = "통화 코드") String currencyCode,
            @Schema(description = "담당자명") String managerName,
            @Schema(description = "결재 상태") String approvalStatus,
            @Schema(description = "요청 상태") String requestStatus,
            @Schema(description = "결재 액션") String approvalAction,
            @Schema(description = "결재 요청자") String approvalRequestedBy,
            @Schema(description = "결재 요청일시") LocalDateTime approvalRequestedAt,
            @Schema(description = "결재 검토 내용") String approvalReview,
            @Schema(description = "품목 스냅샷 (JSON)") String itemsSnapshot,
            @Schema(description = "연결 문서 (JSON)") String linkedDocuments,
            @Schema(description = "특기사항") String remarks,
            @Schema(description = "수정 이력") String revisionHistory,
            @Schema(description = "PI 품목 목록") List<ProformaInvoiceItemResponse> items
    ) {
    }

    @Schema(description = "Commercial Invoice 응답")
    public record CommercialInvoiceResponse(
            @Schema(description = "CI 내부 ID") Long commercialInvoiceId,
            @Schema(description = "CI 문서 ID") String ciId,
            @Schema(description = "PO 내부 ID") Long poId,
            @Schema(description = "송장 발행일") LocalDate invoiceDate,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "통화 ID") Integer currencyId,
            @Schema(description = "총 금액") BigDecimal totalAmount,
            @Schema(description = "CI 상태") String status,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "거래처 주소") String clientAddress,
            @Schema(description = "국가") String country,
            @Schema(description = "통화 코드") String currencyCode,
            @Schema(description = "결제조건") String paymentTerms,
            @Schema(description = "도착항") String portOfDischarge,
            @Schema(description = "바이어") String buyer,
            @Schema(description = "품목 스냅샷 JSON") String itemsSnapshot,
            @Schema(description = "연결 문서 JSON") String linkedDocuments,
            @Schema(description = "생성일시") LocalDateTime createdAt
    ) {
    }

    @Schema(description = "Packing List 응답")
    public record PackingListResponse(
            @Schema(description = "PL 내부 ID") Long packingListId,
            @Schema(description = "PL 문서 ID") String plId,
            @Schema(description = "PO 내부 ID") Long poId,
            @Schema(description = "송장 발행일") LocalDate invoiceDate,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "총 중량") BigDecimal grossWeight,
            @Schema(description = "PL 상태") String status,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "거래처 주소") String clientAddress,
            @Schema(description = "국가") String country,
            @Schema(description = "결제조건") String paymentTerms,
            @Schema(description = "도착항") String portOfDischarge,
            @Schema(description = "바이어") String buyer,
            @Schema(description = "품목 스냅샷 JSON") String itemsSnapshot,
            @Schema(description = "연결 문서 JSON") String linkedDocuments,
            @Schema(description = "생성일시") LocalDateTime createdAt
    ) {
    }

    @Schema(description = "선적지시서 응답")
    public record ShipmentOrderResponse(
            @Schema(description = "선적지시서 ID") String shipmentOrderId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "발행일") LocalDate issueDate,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "담당자 ID") Long managerId,
            @Schema(description = "선적지시서 상태") String status,
            @Schema(description = "납기일") LocalDate dueDate,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "국가") String country,
            @Schema(description = "담당자명") String managerName,
            @Schema(description = "품목명") String itemName,
            @Schema(description = "연결 문서 (JSON)") String linkedDocuments,
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
            @Schema(description = "요청자명") String requesterName,
            @Schema(description = "결재자 ID") Long approverId,
            @Schema(description = "결재자명") String approverName,
            @Schema(description = "코멘트") String comment,
            @Schema(description = "반려 사유") String reason,
            @Schema(description = "검토 스냅샷") String reviewSnapshot,
            @Schema(description = "요청일시") LocalDateTime requestedAt,
            @Schema(description = "검토일시") LocalDateTime reviewedAt,
            @Schema(description = "결재 상태") String status
    ) {
    }

    @Schema(description = "생산지시서 응답")
    public record ProductionOrderResponse(
            @Schema(description = "생산지시서 ID") String productionOrderId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "담당자 ID") Long managerId,
            @Schema(description = "PO 문서 번호") String poNo,
            @Schema(description = "주문일") LocalDate orderDate,
            @Schema(description = "납기일") LocalDate dueDate,
            @Schema(description = "생산지시서 상태") String status,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "국가") String country,
            @Schema(description = "담당자명") String managerName,
            @Schema(description = "품목명") String itemName,
            @Schema(description = "연결 문서 (JSON)") String linkedDocuments,
            @Schema(description = "품목 목록") List<String> items,
            @Schema(description = "품목 스냅샷 (JSON — MO 단계 품목/수량/단가 전이본)") String itemsSnapshot,
            @Schema(description = "생성일시") LocalDateTime createdAt,
            @Schema(description = "수정일시") LocalDateTime updatedAt
    ) {
    }

    @Schema(description = "출하 응답")
    public record ShipmentResponse(
            @Schema(description = "출하 ID") Long shipmentId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "출하지시서 ID") String shipmentOrderId,
            @Schema(description = "출하 상태") String shipmentStatus,
            @Schema(description = "거래처명 (지시서 snapshot)") String clientName,
            @Schema(description = "국가 (지시서 snapshot)") String country,
            @Schema(description = "담당자 (지시서 snapshot)") String managerName,
            @Schema(description = "대표 품목 (지시서 snapshot)") String itemName,
            @Schema(description = "출하 요청일") LocalDate requestDate,
            @Schema(description = "출하 납기일") LocalDate dueDate
    ) {
    }

    @Operation(summary = "PDF 다운로드", description = "문서 데이터를 기준으로 PDF를 즉시 생성해 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF 다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음")
    })
    @GetMapping("/documents/pdf/download")
    public ResponseEntity<byte[]> downloadPdf(
            @Parameter(description = "문서 유형", example = "PO") @RequestParam("docType") String docType,
            @Parameter(description = "문서 ID", example = "PO260001") @RequestParam("documentId") String documentId) {
        byte[] pdf = generatePdf(docType, documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + documentId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] generatePdf(String docType, String documentId) {
        return switch (docType.toUpperCase()) {
            case "PO" -> {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPoCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("PO를 찾을 수 없습니다."));
                yield pdfGenerationService.generatePurchaseOrderPdf(purchaseOrder, purchaseOrder.getItems());
            }
            case "PI" -> {
                ProformaInvoice proformaInvoice = proformaInvoiceRepository.findByPiCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("PI를 찾을 수 없습니다."));
                yield pdfGenerationService.generateProformaInvoicePdf(proformaInvoice, proformaInvoice.getItems());
            }
            case "CI" -> {
                CommercialInvoice commercialInvoice = commercialInvoiceJpaRepository.findByCiCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("CI를 찾을 수 없습니다."));
                yield pdfGenerationService.generateCommercialInvoicePdf(commercialInvoice);
            }
            case "PL" -> {
                PackingList packingList = packingListJpaRepository.findByPlCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("PL을 찾을 수 없습니다."));
                yield pdfGenerationService.generatePackingListPdf(packingList);
            }
            case "SO", "SHIPPING_ORDER" -> {
                ShipmentOrder shipmentOrder = shipmentOrderJpaRepository.findByShipmentOrderCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("SO를 찾을 수 없습니다."));
                yield pdfGenerationService.generateShipmentOrderPdf(shipmentOrder);
            }
            case "MO", "PRODUCTION_ORDER" -> {
                ProductionOrder productionOrder = productionOrderRepository.findByProductionOrderCode(documentId)
                        .orElseThrow(() -> new com.team2.documents.common.error.ResourceNotFoundException("MO를 찾을 수 없습니다."));
                yield pdfGenerationService.generateProductionOrderPdf(productionOrder);
            }
            default -> throw new IllegalArgumentException("지원하지 않는 문서 유형입니다: " + docType);
        };
    }

    @Schema(description = "수금 응답")
    public record CollectionResponse(
            @Schema(description = "수금 ID") Long collectionId,
            @Schema(description = "PO 문서 ID") String poId,
            @Schema(description = "PO 문서 번호") String poNo,
            @Schema(description = "거래처 ID") Integer clientId,
            @Schema(description = "거래처명") String clientName,
            @Schema(description = "국가") String country,
            @Schema(description = "영업 담당자명") String managerName,
            @Schema(description = "총 금액") BigDecimal totalAmount,
            @Schema(description = "수금 완료 금액") BigDecimal collectedAmount,
            @Schema(description = "미수금 금액") BigDecimal remainingAmount,
            @Schema(description = "통화 코드") String currencyCode,
            @Schema(description = "수금 상태") String status,
            @Schema(description = "발행일") LocalDate issueDate,
            @Schema(description = "수금일") LocalDate collectionDate,
            @Schema(description = "생성일시") LocalDateTime createdAt,
            @Schema(description = "수정일시") LocalDateTime updatedAt
    ) {
    }
}
