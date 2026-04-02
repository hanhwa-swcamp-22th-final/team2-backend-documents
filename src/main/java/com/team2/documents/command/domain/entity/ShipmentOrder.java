package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "shipment_orders")
public class ShipmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_order_id", nullable = false)
    private Long shipmentOrderPk;

    @Column(name = "shipment_order_code", nullable = false, unique = true, length = 30)
    private String shipmentOrderCode;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Transient
    private String poCode;

    @Column(name = "shipment_issue_date")
    private LocalDate issueDate;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "shipment_status")
    private String status;

    @Column(name = "shipment_due_date")
    private LocalDate dueDate;

    @Column(name = "shipment_client_name")
    private String clientName;

    @Column(name = "shipment_country")
    private String country;

    @Column(name = "shipment_manager_name")
    private String managerName;

    @Column(name = "shipment_item_name")
    private String itemName;

    @Column(name = "shipment_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public ShipmentOrder() {
    }

    public Long getShipmentOrderPk() {
        return shipmentOrderPk;
    }

    public void setShipmentOrderPk(Long shipmentOrderPk) {
        this.shipmentOrderPk = shipmentOrderPk;
    }

    public String getShipmentOrderId() {
        return shipmentOrderCode;
    }

    public void setShipmentOrderId(String shipmentOrderId) {
        this.shipmentOrderCode = shipmentOrderId;
    }

    public String getShipmentOrderCode() {
        return shipmentOrderCode;
    }

    public void setShipmentOrderCode(String shipmentOrderCode) {
        this.shipmentOrderCode = shipmentOrderCode;
    }

    public String getPoId() {
        return poCode != null ? poCode : (poId == null ? null : String.valueOf(poId));
    }

    public Long getPurchaseOrderId() {
        return poId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCountry() {
        return country;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getItemName() {
        return itemName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkedDocuments() {
        return linkedDocuments;
    }

    public void setLinkedDocuments(String linkedDocuments) {
        this.linkedDocuments = linkedDocuments;
    }
}
