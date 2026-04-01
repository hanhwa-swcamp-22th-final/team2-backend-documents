package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Setter;

@Setter
@Entity
@Table(name = "production_orders")
public class ProductionOrder {

    @Id
    @Column(name = "production_order_id", nullable = false, length = 30)
    private String productionOrderId;

    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Column(name = "production_issue_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "production_due_date")
    private LocalDate dueDate;

    @Column(name = "production_status", nullable = false)
    private String status;

    @Column(name = "production_client_name")
    private String clientName;

    @Column(name = "production_country")
    private String country;

    @Column(name = "production_manager_name")
    private String managerName;

    @Column(name = "production_item_name")
    private String itemName;

    @Column(name = "production_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Transient
    private String poNo;

    @Transient
    private List<String> items;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected ProductionOrder() {
    }

    public ProductionOrder(String productionOrderId,
                           String poId,
                           LocalDate orderDate,
                           Integer clientId,
                           Long managerId,
                           LocalDate dueDate,
                           String status,
                           String clientName,
                           String country,
                           String managerName,
                           String itemName,
                           String linkedDocuments,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.productionOrderId = productionOrderId;
        this.poId = poId;
        this.orderDate = orderDate;
        this.clientId = clientId;
        this.managerId = managerId;
        this.dueDate = dueDate;
        this.status = status;
        this.clientName = clientName;
        this.country = country;
        this.managerName = managerName;
        this.itemName = itemName;
        this.linkedDocuments = linkedDocuments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductionOrder(String productionOrderId,
                           String poId,
                           String poNo,
                           LocalDate orderDate,
                           LocalDate dueDate,
                           String status,
                           List<String> items,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this(productionOrderId, poId, orderDate, 0, null, dueDate, status,
                null, null, null, null, null, createdAt, updatedAt);
        this.poNo = poNo;
        this.items = items;
    }

    public String getProductionOrderId() {
        return productionOrderId;
    }

    public String getPoId() {
        return poId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getPoNo() {
        return poNo;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
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

    public String getLinkedDocuments() {
        return linkedDocuments;
    }

    public List<String> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
