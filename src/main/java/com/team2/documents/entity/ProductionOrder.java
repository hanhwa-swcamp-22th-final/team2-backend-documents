package com.team2.documents.entity;

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

    @Transient
    private String poNo;

    @Column(name = "production_issue_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "production_due_date")
    private LocalDate dueDate;

    @Column(name = "production_status", nullable = false)
    private String status;

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
                           String poNo,
                           LocalDate orderDate,
                           LocalDate dueDate,
                           String status,
                           List<String> items,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.productionOrderId = productionOrderId;
        this.poId = poId;
        this.poNo = poNo;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getProductionOrderId() {
        return productionOrderId;
    }

    public String getPoId() {
        return poId;
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
