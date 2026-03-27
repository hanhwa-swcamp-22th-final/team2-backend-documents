package com.team2.documents.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

@Alias("CollectionView")
public class Collection {

    private final Long id;
    private final String poId;
    private final String poNo;
    private final Long clientId;
    private final String clientName;
    private final BigDecimal totalAmount;
    private BigDecimal collectedAmount;
    private BigDecimal remainingAmount;
    private final String currencyCode;
    private String status;
    private LocalDate collectionDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Collection(Long id,
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
        this.id = id;
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

    public Long getId() {
        return id;
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

    public void completeCollection(LocalDate collectionCompletedDate) {
        if (!"미수금".equals(status)) {
            throw new IllegalStateException("미수금 상태의 현황만 수금완료 처리할 수 있습니다.");
        }
        this.status = "수금완료";
        this.collectionDate = collectionCompletedDate;
    }
}
