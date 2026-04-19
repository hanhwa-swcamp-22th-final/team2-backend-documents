package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.CommercialInvoiceRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.PackingListRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;
import com.team2.documents.common.error.BusinessConflictException;

@Service
@Transactional
public class PurchaseOrderDocumentGenerationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;
    private final PackingListJpaRepository packingListJpaRepository;
    private final CommercialInvoiceRepository commercialInvoiceRepository;
    private final PackingListRepository packingListRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;
    private final ShipmentOrderJpaRepository shipmentOrderJpaRepository;
    private final ShipmentRepository shipmentRepository;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentLinkService documentLinkService;
    private final DocumentRevisionHistoryService documentRevisionHistoryService;
    private final DocsSnapshotService docsSnapshotService;
    private final DocumentAutoMailService documentAutoMailService;
    private final PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService;

    public PurchaseOrderDocumentGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                  CommercialInvoiceJpaRepository commercialInvoiceJpaRepository,
                                                  PackingListJpaRepository packingListJpaRepository,
                                                  CommercialInvoiceRepository commercialInvoiceRepository,
                                                  PackingListRepository packingListRepository,
                                                  ShipmentOrderRepository shipmentOrderRepository,
                                                  ShipmentOrderJpaRepository shipmentOrderJpaRepository,
                                                  ShipmentRepository shipmentRepository,
                                                  DocumentNumberGeneratorService documentNumberGeneratorService,
                                                  DocumentLinkService documentLinkService,
                                                  DocumentRevisionHistoryService documentRevisionHistoryService,
                                                  DocsSnapshotService docsSnapshotService,
                                                  DocumentAutoMailService documentAutoMailService,
                                                  PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.commercialInvoiceJpaRepository = commercialInvoiceJpaRepository;
        this.packingListJpaRepository = packingListJpaRepository;
        this.commercialInvoiceRepository = commercialInvoiceRepository;
        this.packingListRepository = packingListRepository;
        this.shipmentOrderRepository = shipmentOrderRepository;
        this.shipmentOrderJpaRepository = shipmentOrderJpaRepository;
        this.shipmentRepository = shipmentRepository;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentLinkService = documentLinkService;
        this.documentRevisionHistoryService = documentRevisionHistoryService;
        this.docsSnapshotService = docsSnapshotService;
        this.documentAutoMailService = documentAutoMailService;
        this.purchaseOrderProductionOrderGenerationService = purchaseOrderProductionOrderGenerationService;
    }

    public void generateOnConfirmation(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new BusinessConflictException("확정 상태의 PO만 자동 생성 문서를 가질 수 있습니다.");
        }

        String ciId = documentNumberGeneratorService.nextCommercialInvoiceId();
        String plId = documentNumberGeneratorService.nextPackingListId();
        String shipmentOrderId = documentNumberGeneratorService.nextShipmentOrderId();

        commercialInvoiceRepository.createFromPurchaseOrder(poId, ciId);
        packingListRepository.createFromPurchaseOrder(poId, plId);
        shipmentOrderRepository.createFromPurchaseOrder(poId, shipmentOrderId);

        commercialInvoiceJpaRepository.findByCiCode(ciId)
                .ifPresent(ci -> docsSnapshotService.saveCommercialInvoiceSnapshot(ci, purchaseOrder));
        packingListJpaRepository.findByPlCode(plId)
                .ifPresent(pl -> docsSnapshotService.savePackingListSnapshot(pl, purchaseOrder));
        shipmentOrderJpaRepository.findByShipmentOrderCode(shipmentOrderId)
                .ifPresent(so -> {
                    // Step C — PO 등록 시 지정한 출하 담당자를 SO.managerId 로 전이.
                    // 지정되지 않았으면 PO managerId 를 기본 fallback (영업담당자 = 기본 출하 책임자).
                    Long assignee = purchaseOrder.getShippingAssigneeId() != null
                            ? purchaseOrder.getShippingAssigneeId()
                            : purchaseOrder.getManagerId();
                    if (assignee != null) {
                        so.setManagerId(assignee);
                        shipmentOrderJpaRepository.save(so);
                    }
                    documentAutoMailService.sendShipmentOrderToShippingTeam(purchaseOrder, so);
                    // Shipment stub 은 PurchaseOrderCreationService.createInitialViews 에서
                    // shipment_order_id=NULL 로 먼저 저장됨. SO 가 생성된 뒤에야 FK 를 채울
                    // 수 있는데 기존 경로가 이 링크를 걸어주지 않아 ShipmentQueryMapper 의
                    // LEFT JOIN so 가 NULL 만 돌려주고 거래처/국가/담당자/품목이 전부 빈값으로
                    // 노출됐음(QA 3차 NEW-1 blocker). 출하완료 버튼 disabled 의 실 원인.
                    // 여기서 FK 와 Shipment 쪽 자체 컬럼(clientId/요청일/납기일) 까지 주입.
                    shipmentRepository.findByPoCode(poId).ifPresent(shipment -> {
                        shipment.setShipmentOrderId(so.getShipmentOrderPk());
                        if (shipment.getClientId() == null) {
                            shipment.setClientId(purchaseOrder.getClientId());
                        }
                        if (shipment.getShipmentRequestDate() == null) {
                            shipment.setShipmentRequestDate(java.time.LocalDate.now());
                        }
                        if (shipment.getShipmentDueDate() == null) {
                            shipment.setShipmentDueDate(purchaseOrder.getDeliveryDate());
                        }
                        shipmentRepository.save(shipment);
                    });
                });

        documentLinkService.linkAutoGeneratedDocuments(poId, ciId, plId, shipmentOrderId);

        // Step C — PO 등록 시 'PRODUCTION' 을 선택한 경우 MO 도 자동 발행.
        // 'DIRECT' 또는 null 은 기존 흐름(직출하).
        String route = purchaseOrder.getProductionRoute();
        if ("PRODUCTION".equalsIgnoreCase(String.valueOf(route))) {
            Long assigneeId = purchaseOrder.getProductionAssigneeId();
            purchaseOrderProductionOrderGenerationService.generate(
                    poId,
                    assigneeId,
                    null,
                    purchaseOrder.getManagerId()
            );
        }

        documentRevisionHistoryService.recordPurchaseOrderEvent(
                poId,
                "GENERATE_FOLLOW_UP_DOCUMENTS",
                purchaseOrder.getManagerId(),
                purchaseOrder.getStatus().name(),
                "CI/PL/SO 문서를 자동 생성했습니다."
                        + ("PRODUCTION".equalsIgnoreCase(String.valueOf(route))
                            ? " + 생산 경유 선택으로 MO 자동 발행."
                            : "")
        );
    }
}
