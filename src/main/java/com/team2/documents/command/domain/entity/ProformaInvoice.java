package com.team2.documents.command.domain.entity;

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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "proforma_invoices")
public class ProformaInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pi_id", nullable = false)
    private Long proformaInvoiceId;

    @Column(name = "pi_code", nullable = false, unique = true, length = 30)
    private String piCode;

    @Column(name = "pi_issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "client_id", nullable = false)
    private Integer clientId;

    @Column(name = "currency_id", nullable = false)
    private Integer currencyId;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pi_status", nullable = false)
    private ProformaInvoiceStatus status;

    @Column(name = "pi_delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "pi_incoterms_code")
    private String incotermsCode;

    @Column(name = "pi_named_place")
    private String namedPlace;

    @Column(name = "pi_total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "pi_client_name")
    private String clientName;

    @Column(name = "pi_client_address", columnDefinition = "TEXT")
    private String clientAddress;

    @Column(name = "pi_country")
    private String country;

    @Column(name = "pi_currency_code")
    private String currencyCode;

    @Column(name = "pi_manager_name")
    private String managerName;

    @Column(name = "pi_approval_status")
    private String approvalStatus;

    @Column(name = "pi_request_status")
    private String requestStatus;

    @Column(name = "pi_approval_action")
    private String approvalAction;

    @Column(name = "pi_approval_requested_by")
    private String approvalRequestedBy;

    @Column(name = "pi_approval_requested_at")
    private LocalDateTime approvalRequestedAt;

    @Column(name = "pi_approval_review", columnDefinition = "TEXT")
    private String approvalReview;

    @Column(name = "pi_items_snapshot", columnDefinition = "TEXT")
    private String itemsSnapshot;

    @Column(name = "pi_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pi_id", referencedColumnName = "pi_id")
    private List<ProformaInvoiceItem> items = new ArrayList<>();

    protected ProformaInvoice() {
    }

    public ProformaInvoice(String piId, ProformaInvoiceStatus status) {
        this.piCode = piId;
        this.status = status;
        this.issueDate = LocalDate.now();
        this.clientId = 0;
        this.currencyId = 0;
        this.managerId = 0L;
        this.totalAmount = BigDecimal.ZERO;
    }

    public ProformaInvoice(String piId,
                           LocalDate issueDate,
                           Integer clientId,
                           Integer currencyId,
                           Long managerId,
                           ProformaInvoiceStatus status,
                           LocalDate deliveryDate,
                           String incotermsCode,
                           String namedPlace,
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
                           List<ProformaInvoiceItem> items) {
        this.piCode = piId;
        this.issueDate = issueDate;
        this.clientId = clientId;
        this.currencyId = currencyId;
        this.managerId = managerId;
        this.status = status;
        this.deliveryDate = deliveryDate;
        this.incotermsCode = incotermsCode;
        this.namedPlace = namedPlace;
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
        replaceItems(items);
    }

    @PrePersist
    void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (status == null) {
            status = ProformaInvoiceStatus.DRAFT;
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

    public void replaceItems(List<ProformaInvoiceItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
    }

    public Long getProformaInvoiceId() {
        return proformaInvoiceId;
    }

    public String getPiCode() {
        return piCode;
    }

    public String getPiId() {
        return piCode;
    }

    public void setPiId(String piId) {
        this.piCode = piId;
    }

    public ProformaInvoiceStatus getStatus() {
        return status;
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

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getIncotermsCode() {
        return incotermsCode;
    }

    public String getNamedPlace() {
        return namedPlace;
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

    public List<ProformaInvoiceItem> getItems() {
        return items;
    }
}
