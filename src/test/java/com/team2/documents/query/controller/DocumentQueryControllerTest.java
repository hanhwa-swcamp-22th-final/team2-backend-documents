package com.team2.documents.query.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.team2.documents.infrastructure.s3.S3Service;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.query.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.query.model.CollectionView;
import com.team2.documents.query.model.ProductionOrderView;
import com.team2.documents.query.model.PurchaseOrderView;
import com.team2.documents.query.model.ShipmentView;
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
    private S3Service s3Service;

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
        when(productionOrderQueryService.findAll()).thenReturn(java.util.List.of(productionOrder));

        mockMvc.perform(get("/api/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].productionOrderId").value("MO260001"))
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO260001"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("진행중"));

        verify(productionOrderQueryService).findAll();
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
        when(shipmentQueryService.findAll()).thenReturn(java.util.List.of(shipment));

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].shipmentId").value(1))
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO2025-0001"))
                .andExpect(jsonPath("$._embedded.*[0].shipmentStatus").value("READY"));

        verify(shipmentQueryService).findAll();
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
        when(collectionQueryService.findAll()).thenReturn(java.util.List.of(collection));

        mockMvc.perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].poId").value("PO260001"))
                .andExpect(jsonPath("$._embedded.*[0].clientName").value("ABC Trading"))
                .andExpect(jsonPath("$._embedded.*[0].status").value("미수금"));

        verify(collectionQueryService).findAll();
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
