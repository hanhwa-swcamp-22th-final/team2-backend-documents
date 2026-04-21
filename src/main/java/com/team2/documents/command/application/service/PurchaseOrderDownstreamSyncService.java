package com.team2.documents.command.application.service;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderJpaRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderDownstreamSyncService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ShipmentOrderJpaRepository shipmentOrderJpaRepository;
    private final ShipmentRepository shipmentRepository;

    public void syncDueDates(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null || purchaseOrder.getPurchaseOrderId() == null) {
            return;
        }
        LocalDate deliveryDate = purchaseOrder.getDeliveryDate();
        Long poId = purchaseOrder.getPurchaseOrderId();

        productionOrderRepository.findByPoId(poId)
                .ifPresent(productionOrder -> syncProductionDueDate(productionOrder, deliveryDate));
        shipmentOrderJpaRepository.findByPoId(poId)
                .ifPresent(shipmentOrder -> syncShipmentOrderDueDate(shipmentOrder, deliveryDate));
        shipmentRepository.findByPoId(poId)
                .ifPresent(shipment -> syncShipmentDueDate(shipment, deliveryDate));
    }

    private void syncProductionDueDate(ProductionOrder productionOrder, LocalDate deliveryDate) {
        if (Objects.equals(productionOrder.getDueDate(), deliveryDate)) {
            return;
        }
        productionOrder.setDueDate(deliveryDate);
        productionOrderRepository.save(productionOrder);
    }

    private void syncShipmentOrderDueDate(ShipmentOrder shipmentOrder, LocalDate deliveryDate) {
        if (Objects.equals(shipmentOrder.getDueDate(), deliveryDate)) {
            return;
        }
        shipmentOrder.setDueDate(deliveryDate);
        shipmentOrderJpaRepository.save(shipmentOrder);
    }

    private void syncShipmentDueDate(Shipment shipment, LocalDate deliveryDate) {
        if (Objects.equals(shipment.getShipmentDueDate(), deliveryDate)) {
            return;
        }
        shipment.setShipmentDueDate(deliveryDate);
        shipmentRepository.save(shipment);
    }
}
