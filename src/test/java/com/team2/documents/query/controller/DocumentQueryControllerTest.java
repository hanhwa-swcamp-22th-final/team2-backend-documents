package com.team2.documents.query.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.team2.documents.command.application.service.UserSnapshotService;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.command.infrastructure.client.AuthFeignClient;
import com.team2.documents.infrastructure.pdf.PdfGenerationService;
import com.team2.documents.query.dto.PagedResult;
import com.team2.documents.query.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.query.model.CollectionView;
import com.team2.documents.query.model.CommercialInvoiceView;
import com.team2.documents.query.model.PackingListView;
import com.team2.documents.query.model.ProductionOrderView;
import com.team2.documents.query.model.PurchaseOrderView;
import com.team2.documents.query.model.ShipmentView;
import com.team2.documents.query.model.ShipmentOrderView;
import com.team2.documents.query.service.ApprovalRequestQueryService;
import com.team2.documents.query.service.CollectionQueryService;
import com.team2.documents.query.service.CommercialInvoiceQueryService;
import com.team2.documents.query.service.DocsRevisionQueryService;
import com.team2.documents.query.service.PackingListQueryService;
import com.team2.documents.query.service.ProformaInvoiceQueryService;
import com.team2.documents.query.service.ProductionOrderQueryService;
import com.team2.documents.query.service.PurchaseOrderQueryService;
import com.team2.documents.query.service.ShipmentOrderQueryService;
import com.team2.documents.query.service.ShipmentQueryService;

@WebMvcTest(DocumentQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseOrderQueryService purchaseOrderQueryService;

    @MockitoBean
    private ProformaInvoiceQueryService proformaInvoiceQueryService;

    @MockitoBean
    private CommercialInvoiceQueryService commercialInvoiceQueryService;

    @MockitoBean
    private PackingListQueryService packingListQueryService;

    @MockitoBean
    private ShipmentOrderQueryService shipmentOrderQueryService;

    @MockitoBean
    private ProductionOrderQueryService productionOrderQueryService;

    @MockitoBean
    private ShipmentQueryService shipmentQueryService;

    @MockitoBean
    private CollectionQueryService collectionQueryService;

    @MockitoBean
    private ApprovalRequestQueryService approvalRequestQueryService;

    @MockitoBean
    private DocsRevisionQueryService docsRevisionQueryService;

    @MockitoBean
    private PdfGenerationService pdfGenerationService;

    @MockitoBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockitoBean
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @MockitoBean
    private CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;

    @MockitoBean
    private PackingListJpaRepository packingListJpaRepository;

    @MockitoBean
    private ShipmentOrderJpaRepository shipmentOrderJpaRepository;

    @MockitoBean
    private ProductionOrderRepository productionOrderRepository;

    @MockitoBean
    private UserSnapshotService userSnapshotService;

    @MockitoBean
    private AuthFeignClient authFeignClient;

    @Test
    @DisplayName("PDF 다운로드 API 호출 시 문서 PDF를 즉시 생성해 반환한다")
    void downloadPdf_whenS3KeyExists_thenReturnsPdfBytes() throws Exception {
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO260001", PurchaseOrderStatus.CONFIRMED);
        byte[] pdfBytes = "%PDF-1.4 sample".getBytes();
        when(purchaseOrderRepository.findByPoCode("PO260001")).thenReturn(java.util.Optional.of(purchaseOrder));
        when(pdfGenerationService.generatePurchaseOrderPdf(purchaseOrder, purchaseOrder.getItems())).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/documents/pdf/download")
                        .param("docType", "PO")
                        .param("documentId", "PO260001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"PO260001.pdf\""))
                .andExpect(content().bytes(pdfBytes));

        verify(purchaseOrderRepository).findByPoCode("PO260001");
        verify(pdfGenerationService).generatePurchaseOrderPdf(purchaseOrder, purchaseOrder.getItems());
    }

    @Test
    @DisplayName("PO 단건 조회 API 호출 시 200 OK와 PO를 반환한다")
    void getPurchaseOrderApi_whenPurchaseOrderExists_thenReturnsOkAndPurchaseOrder() throws Exception {
        PurchaseOrderView purchaseOrder = new PurchaseOrderView();
        purchaseOrder.setPoId("PO2025-0001");
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT.name());
        when(purchaseOrderQueryService.findById("PO2025-0001")).thenReturn(purchaseOrder);
        when(docsRevisionQueryService.getRevisionHistory("PO", null)).thenReturn("[]");

        mockMvc.perform(get("/api/purchase-orders/{poId}", "PO2025-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO2025-0001"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(purchaseOrderQueryService).findById("PO2025-0001");
    }

    @Test
    @DisplayName("CI 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getCommercialInvoicesApi_whenCommercialInvoicesExist_thenReturnsOkAndList() throws Exception {
        CommercialInvoiceView commercialInvoice = new CommercialInvoiceView();
        commercialInvoice.setCiId("CI260001");
        commercialInvoice.setStatus("CONFIRMED");
        when(commercialInvoiceQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(List.of(commercialInvoice), 1L));

        mockMvc.perform(get("/api/commercial-invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].ciId").value("CI260001"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("CONFIRMED"));

        verify(commercialInvoiceQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("CI 단건 조회 API 호출 시 200 OK와 CI를 반환한다")
    void getCommercialInvoiceApi_whenCommercialInvoiceExists_thenReturnsOkAndCommercialInvoice() throws Exception {
        CommercialInvoiceView commercialInvoice = new CommercialInvoiceView();
        commercialInvoice.setCiId("CI260001");
        commercialInvoice.setStatus("CONFIRMED");
        when(commercialInvoiceQueryService.findById("CI260001")).thenReturn(commercialInvoice);

        mockMvc.perform(get("/api/commercial-invoices/{ciId}", "CI260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciId").value("CI260001"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(commercialInvoiceQueryService).findById("CI260001");
    }

    @Test
    @DisplayName("PL 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getPackingListsApi_whenPackingListsExist_thenReturnsOkAndList() throws Exception {
        PackingListView packingList = new PackingListView();
        packingList.setPlId("PL260001");
        packingList.setStatus("CREATED");
        when(packingListQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(List.of(packingList), 1L));

        mockMvc.perform(get("/api/packing-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].plId").value("PL260001"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("CREATED"));

        verify(packingListQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("PL 단건 조회 API 호출 시 200 OK와 PL을 반환한다")
    void getPackingListApi_whenPackingListExists_thenReturnsOkAndPackingList() throws Exception {
        PackingListView packingList = new PackingListView();
        packingList.setPlId("PL260001");
        packingList.setStatus("CREATED");
        when(packingListQueryService.findById("PL260001")).thenReturn(packingList);

        mockMvc.perform(get("/api/packing-lists/{plId}", "PL260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plId").value("PL260001"))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(packingListQueryService).findById("PL260001");
    }

    @Test
    @DisplayName("SO 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getShipmentOrdersApi_whenShipmentOrdersExist_thenReturnsOkAndList() throws Exception {
        ShipmentOrderView shipmentOrder = new ShipmentOrderView();
        shipmentOrder.setShipmentOrderId("SO260001");
        shipmentOrder.setPoId("PO260001");
        shipmentOrder.setStatus("READY");
        when(shipmentOrderQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(List.of(shipmentOrder), 1L));

        mockMvc.perform(get("/api/shipment-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].shipmentOrderId").value("SO260001"))
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO260001"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("READY"));

        verify(shipmentOrderQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("SO 단건 조회 API 호출 시 200 OK와 SO를 반환한다")
    void getShipmentOrderApi_whenShipmentOrderExists_thenReturnsOkAndShipmentOrder() throws Exception {
        ShipmentOrderView shipmentOrder = new ShipmentOrderView();
        shipmentOrder.setShipmentOrderId("SO260001");
        shipmentOrder.setPoId("PO260001");
        shipmentOrder.setStatus("READY");
        when(shipmentOrderQueryService.findById("SO260001")).thenReturn(shipmentOrder);

        mockMvc.perform(get("/api/shipment-orders/{shipmentOrderId}", "SO260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentOrderId").value("SO260001"))
                .andExpect(jsonPath("$.poId").value("PO260001"))
                .andExpect(jsonPath("$.status").value("READY"));

        verify(shipmentOrderQueryService).findById("SO260001");
    }

    @Test
    @DisplayName("PO 초기 상태 조회 API 호출 시 200 OK와 상태값을 반환한다")
    void determineInitialStatusApi_whenRequestIsValid_thenReturnsOkAndStatus() throws Exception {
        when(purchaseOrderQueryService.determineInitialStatus(1L)).thenReturn(PurchaseOrderStatus.DRAFT);

        mockMvc.perform(get("/api/purchase-orders/initial-status/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(purchaseOrderQueryService).determineInitialStatus(1L);
    }

    @Test
    @DisplayName("생산지시서 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getProductionOrdersApi_whenProductionOrdersExist_thenReturnsOkAndList() throws Exception {
        ProductionOrderView productionOrder = new ProductionOrderView();
        productionOrder.setProductionOrderId("MO260001");
        productionOrder.setPoId("PO260001");
        productionOrder.setPoNo("PO260001");
        productionOrder.setStatus("진행중");
        when(productionOrderQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(java.util.List.of(productionOrder), 1L));

        mockMvc.perform(get("/api/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].productionOrderId").value("MO260001"))
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO260001"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("진행중"));

        verify(productionOrderQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("생산지시서 단건 조회 API 호출 시 200 OK와 생산지시서를 반환한다")
    void getProductionOrderApi_whenProductionOrderExists_thenReturnsOkAndProductionOrder() throws Exception {
        ProductionOrderView productionOrder = new ProductionOrderView();
        productionOrder.setProductionOrderId("MO260001");
        productionOrder.setPoId("PO260001");
        productionOrder.setPoNo("PO260001");
        productionOrder.setStatus("진행중");
        when(productionOrderQueryService.findById("MO260001")).thenReturn(productionOrder);

        mockMvc.perform(get("/api/production-orders/{id}", "MO260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productionOrderId").value("MO260001"))
                .andExpect(jsonPath("$.poId").value("PO260001"))
                .andExpect(jsonPath("$.status").value("진행중"));

        verify(productionOrderQueryService).findById("MO260001");
    }

    @Test
    @DisplayName("출하현황 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getShipmentsApi_whenShipmentsExist_thenReturnsOkAndList() throws Exception {
        ShipmentView shipment = new ShipmentView();
        shipment.setShipmentId(1L);
        shipment.setPoId("PO2025-0001");
        shipment.setShipmentStatus("READY");
        when(shipmentQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(java.util.List.of(shipment), 1L));

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].shipmentId").value(1))
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO2025-0001"))
                .andExpect(jsonPath("$._embedded.*[0].shipmentStatus").value("READY"));

        verify(shipmentQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("출하현황 단건 조회 API 호출 시 200 OK와 출하현황을 반환한다")
    void getShipmentApi_whenShipmentExists_thenReturnsOkAndShipment() throws Exception {
        ShipmentView shipment = new ShipmentView();
        shipment.setShipmentId(1L);
        shipment.setPoId("PO2025-0001");
        shipment.setShipmentStatus("READY");
        when(shipmentQueryService.findById(1L)).thenReturn(shipment);

        mockMvc.perform(get("/api/shipments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value(1))
                .andExpect(jsonPath("$.poId").value("PO2025-0001"))
                .andExpect(jsonPath("$.shipmentStatus").value("READY"));

        verify(shipmentQueryService).findById(1L);
    }

    @Test
    @DisplayName("매출·수금 현황 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getCollectionsApi_whenCollectionsExist_thenReturnsOkAndList() throws Exception {
        CollectionView collection = new CollectionView();
        collection.setCollectionId(1L);
        collection.setPoId("PO260001");
        collection.setPoNo("PO260001");
        collection.setClientId(1L);
        collection.setClientName("ABC Trading");
        collection.setTotalAmount(new BigDecimal("15000.00"));
        collection.setCollectedAmount(new BigDecimal("10000.00"));
        collection.setRemainingAmount(new BigDecimal("5000.00"));
        collection.setCurrencyCode("USD");
        collection.setStatus("미수금");
        when(collectionQueryService.findAll(anyInt(), anyInt()))
                .thenReturn(new PagedResult<>(java.util.List.of(collection), 1L));

        mockMvc.perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO260001"))
                .andExpect(jsonPath("$._embedded.*[0].clientName").value("ABC Trading"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("미수금"));

        verify(collectionQueryService).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("매출·수금 현황 단건 조회 API 호출 시 200 OK와 현황을 반환한다")
    void getCollectionApi_whenCollectionExists_thenReturnsOkAndCollection() throws Exception {
        CollectionView collection = new CollectionView();
        collection.setCollectionId(1L);
        collection.setPoId("PO260001");
        collection.setPoNo("PO260001");
        collection.setClientId(1L);
        collection.setClientName("ABC Trading");
        collection.setTotalAmount(new BigDecimal("15000.00"));
        collection.setCollectedAmount(new BigDecimal("10000.00"));
        collection.setRemainingAmount(new BigDecimal("5000.00"));
        collection.setCurrencyCode("USD");
        collection.setStatus("미수금");
        when(collectionQueryService.findById(1L)).thenReturn(collection);

        mockMvc.perform(get("/api/collections/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO260001"))
                .andExpect(jsonPath("$.clientName").value("ABC Trading"))
                .andExpect(jsonPath("$.status").value("미수금"));

        verify(collectionQueryService).findById(1L);
    }
}
