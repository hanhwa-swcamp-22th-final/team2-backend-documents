package com.team2.documents.command.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import jakarta.persistence.Column;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Setter
@Alias("CollectionView")
@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long collectionId;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Transient
    private String poCode;

    // DDL 은 client_id INT 이고 다른 엔티티(ProformaInvoice/PurchaseOrder)는 Integer 로
    // 선언돼 있다. 본 엔티티만 Long 으로 드리프트돼 있으나 Hibernate 가 INT ↔ Long 자동
    // 변환하므로 런타임 문제 없음. 타입 통일은 Command/Query DTO 의 다수 호출처 연쇄
    // 수정 필요해 별도 리팩토링으로 분리.
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "currency_id")
    private Integer currencyId;

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

    @Column(name = "collection_status")
    private String status;

    @Column(name = "collection_issue_date")
    private LocalDate collectionIssueDate;

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
        this(collectionId, 0L, poId, clientId, clientName, totalAmount,
                collectedAmount, remainingAmount, currencyCode, status, collectionDate, createdAt, updatedAt);
    }

    public Collection(Long collectionId,
                      Long poId,
                      String poCode,
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
        this.poCode = poCode;
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
        return poCode != null ? poCode : (poId == null ? null : String.valueOf(poId));
    }

    public String getPoNo() {
        return poCode;
    }

    public Long getPurchaseOrderId() {
        return poId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public LocalDate getCollectionIssueDate() {
        return collectionIssueDate;
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
