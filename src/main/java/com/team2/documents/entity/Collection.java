package com.team2.documents.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Alias("CollectionView")
@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long collectionId;

    @Column(name = "po_id", nullable = false)
    private String poId;

    @Transient
    private String poNo;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Transient
    private String clientName;

    @Column(name = "collection_sales_amount", nullable = false)
    private BigDecimal totalAmount;

    @Transient
    private BigDecimal collectedAmount;

    @Transient
    private BigDecimal remainingAmount;

    @Transient
    private String currencyCode;

    private String status;

    @Column(name = "collection_completed_date")
    private LocalDate collectionDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Collection() {
    }

    public Collection(Long collectionId,
                      String poId,
                      String poNo,
                      Long clientId,
                      String clientName,
                      BigDecimal totalAmount,
                      BigDecimal collectedAmount,
                      BigDecimal remainingAmount,
                      String currencyCode,
                      String status,
                      LocalDate collectionDate,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
        this.collectionId = collectionId;
        this.poId = poId;
        this.poNo = poNo;
        this.clientId = clientId;
        this.clientName = clientName;
        this.totalAmount = totalAmount;
        this.collectedAmount = collectedAmount;
        this.remainingAmount = remainingAmount;
        this.currencyCode = currencyCode;
        this.status = status;
        this.collectionDate = collectionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public String getPoId() {
        return poId;
    }

    public String getPoNo() {
        return poNo;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getCollectedAmount() {
        return collectedAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
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

    public void setCollectionDate(LocalDate collectionCompletedDate) {
        this.collectionDate = collectionCompletedDate;
    }
}
