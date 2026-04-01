package com.team2.documents.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import com.team2.documents.entity.enums.PurchaseOrderStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Column(name = "pi_id", length = 30)
    private String piId;

    @Column(name = "po_issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "currency_id", nullable = false)
    private Integer currencyId;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "po_status", nullable = false)
    private PurchaseOrderStatus status;

    @Column(name = "po_delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "po_incoterms_code", length = 10)
    private String incotermsCode;

    @Column(name = "po_named_place", length = 200)
    private String namedPlace;

    @Column(name = "po_source_delivery_date")
    private LocalDate sourceDeliveryDate;

    @Column(name = "po_delivery_date_override", nullable = false)
    private boolean deliveryDateOverride;

    @Column(name = "po_total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "po_client_name", length = 200)
    private String clientName;

    @Column(name = "po_client_address", columnDefinition = "TEXT")
    private String clientAddress;

    @Column(name = "po_country", length = 100)
    private String country;

    @Column(name = "po_currency_code", length = 10)
    private String currencyCode;

    @Column(name = "po_manager_name", length = 100)
    private String managerName;

    @Column(name = "po_approval_status", length = 20)
    private String approvalStatus;

    @Column(name = "po_request_status", length = 20)
    private String requestStatus;

    @Column(name = "po_approval_action", length = 20)
    private String approvalAction;

    @Column(name = "po_approval_requested_by", length = 100)
    private String approvalRequestedBy;

    @Column(name = "po_approval_requested_at")
    private LocalDateTime approvalRequestedAt;

    @Column(name = "po_approval_review", columnDefinition = "TEXT")
    private String approvalReview;

    @Column(name = "po_items_snapshot", columnDefinition = "TEXT")
    private String itemsSnapshot;

    @Column(name = "po_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "po_revision_history", columnDefinition = "TEXT")
    private String revisionHistory;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "po_id", referencedColumnName = "po_id")
    private List<PurchaseOrderItem> items = new ArrayList<>();

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String poId) {
        this(poId, PurchaseOrderStatus.DRAFT);
    }

    public PurchaseOrder(String poId, PurchaseOrderStatus status) {
        this.poId = poId;
        this.status = status;
        this.issueDate = LocalDate.now();
        this.clientId = 0;
        this.currencyId = 0;
        this.managerId = 0L;
        this.totalAmount = BigDecimal.ZERO;
    }

    public PurchaseOrder(String poId,
                         String piId,
                         LocalDate issueDate,
                         Integer clientId,
                         Integer currencyId,
                         Long managerId,
                         PurchaseOrderStatus status,
                         LocalDate deliveryDate,
                         String incotermsCode,
                         String namedPlace,
                         LocalDate sourceDeliveryDate,
                         boolean deliveryDateOverride,
                         BigDecimal totalAmount,
                         String clientName,
                         String clientAddress,
                         String country,
                         String currencyCode,
                         String managerName,
                         String approvalStatus,
                         String requestStatus,
                         String approvalAction,
                         String approvalRequestedBy,
                         LocalDateTime approvalRequestedAt,
                         String approvalReview,
                         String itemsSnapshot,
                         String linkedDocuments,
                         String revisionHistory,
                         List<PurchaseOrderItem> items) {
        this.poId = poId;
        this.piId = piId;
        this.issueDate = issueDate;
        this.clientId = clientId;
        this.currencyId = currencyId;
        this.managerId = managerId;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.incotermsCode = incotermsCode;
        this.namedPlace = namedPlace;
        this.sourceDeliveryDate = sourceDeliveryDate;
        this.deliveryDateOverride = deliveryDateOverride;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.country = country;
        this.currencyCode = currencyCode;
        this.managerName = managerName;
        this.approvalStatus = approvalStatus;
        this.requestStatus = requestStatus;
        this.approvalAction = approvalAction;
        this.approvalRequestedBy = approvalRequestedBy;
        this.approvalRequestedAt = approvalRequestedAt;
        this.approvalReview = approvalReview;
        this.itemsSnapshot = itemsSnapshot;
        this.linkedDocuments = linkedDocuments;
        this.revisionHistory = revisionHistory;
        replaceItems(items);
    }

    @PrePersist
    void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (status == null) {
            status = PurchaseOrderStatus.DRAFT;
        }
        if (clientId == null) {
            clientId = 0;
        }
        if (currencyId == null) {
            currencyId = 0;
        }
        if (managerId == null) {
            managerId = 0L;
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    public void replaceItems(List<PurchaseOrderItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
    }

    public String getPoId() {
        return poId;
    }

    public String getPiId() {
        return piId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getIncotermsCode() {
        return incotermsCode;
    }

    public String getNamedPlace() {
        return namedPlace;
    }

    public LocalDate getSourceDeliveryDate() {
        return sourceDeliveryDate;
    }

    public boolean isDeliveryDateOverride() {
        return deliveryDateOverride;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getCountry() {
        return country;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public String getApprovalAction() {
        return approvalAction;
    }

    public String getApprovalRequestedBy() {
        return approvalRequestedBy;
    }

    public LocalDateTime getApprovalRequestedAt() {
        return approvalRequestedAt;
    }

    public String getApprovalReview() {
        return approvalReview;
    }

    public String getItemsSnapshot() {
        return itemsSnapshot;
    }

    public String getLinkedDocuments() {
        return linkedDocuments;
    }

    public String getRevisionHistory() {
        return revisionHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }
}
