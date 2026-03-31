package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.repository.CommercialInvoiceRepository;
import com.team2.documents.command.repository.PackingListRepository;
import com.team2.documents.command.repository.ShipmentOrderRepository;

@Service
@Transactional
public class PurchaseOrderDocumentGenerationService {

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final CommercialInvoiceRepository commercialInvoiceRepository;
    private final PackingListRepository packingListRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;

    public PurchaseOrderDocumentGenerationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                                  CommercialInvoiceRepository commercialInvoiceRepository,
                                                  PackingListRepository packingListRepository,
                                                  ShipmentOrderRepository shipmentOrderRepository) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.commercialInvoiceRepository = commercialInvoiceRepository;
        this.packingListRepository = packingListRepository;
        this.shipmentOrderRepository = shipmentOrderRepository;
    }

    public void generateOnConfirmation(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 자동 생성 문서를 가질 수 있습니다.");
        }

        commercialInvoiceRepository.createFromPurchaseOrder(poId);
        packingListRepository.createFromPurchaseOrder(poId);
        shipmentOrderRepository.createFromPurchaseOrder(poId);
    }
}
