package com.team2.documents.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProductionOrder {

    private final Long id;
    private final String productionOrderNo;
    private final String poId;
    private final String poNo;
    private final LocalDate orderDate;
    private final LocalDate dueDate;
    private final String status;
    private final List<String> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ProductionOrder(Long id,
                           String productionOrderNo,
                           String poId,
                           String poNo,
                           LocalDate orderDate,
                           LocalDate dueDate,
                           String status,
                           List<String> items,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.id = id;
        this.productionOrderNo = productionOrderNo;
        this.poId = poId;
        this.poNo = poNo;
        this.orderDate = orderDate;
        this.dueDate = dueDate;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getProductionOrderNo() {
        return productionOrderNo;
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
