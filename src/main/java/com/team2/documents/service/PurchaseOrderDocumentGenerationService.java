package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.CommercialInvoiceRepository;
import com.team2.documents.repository.PackingListRepository;
import com.team2.documents.repository.PurchaseOrderRepository;
import com.team2.documents.repository.ShipmentOrderRepository;

@Service
public class PurchaseOrderDocumentGenerationService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CommercialInvoiceRepository commercialInvoiceRepository;
    private final PackingListRepository packingListRepository;
    private final ShipmentOrderRepository shipmentOrderRepository;

    public PurchaseOrderDocumentGenerationService(PurchaseOrderRepository purchaseOrderRepository,
                                                  CommercialInvoiceRepository commercialInvoiceRepository,
                                                  PackingListRepository packingListRepository,
                                                  ShipmentOrderRepository shipmentOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.commercialInvoiceRepository = commercialInvoiceRepository;
        this.packingListRepository = packingListRepository;
        this.shipmentOrderRepository = shipmentOrderRepository;
    }

    public void generateOnConfirmation(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));
        if (!PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("확정 상태의 PO만 자동 생성 문서를 가질 수 있습니다.");
        }

        commercialInvoiceRepository.createFromPurchaseOrder(poId);
        packingListRepository.createFromPurchaseOrder(poId);
        shipmentOrderRepository.createFromPurchaseOrder(poId);
    }
}
