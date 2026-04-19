package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Setter;

@Setter
@Entity
@Table(name = "production_orders")
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "production_order_id", nullable = false)
    private Long productionOrderPk;

    @Column(name = "production_order_code", nullable = false, unique = true, length = 30)
    private String productionOrderCode;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Transient
    private String poCode;

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

    // PO 에서 전이된 품목 스냅샷(JSON 배열: {itemName,quantity,unit,unitPrice,amount,remark}).
    // NEW-6: MO 에 전용 items 테이블이 없어 화면이 "총수량 0EA, 품목 빈" 으로 보였다.
    // ddl-auto=update 로 자동 추가. 기존 레코드는 NULL.
    @Column(name = "production_items_snapshot", columnDefinition = "TEXT")
    private String itemsSnapshot;

    @Transient
    private List<String> items;

    // created_at/updated_at 는 DB 기본값(CURRENT_TIMESTAMP / ON UPDATE CURRENT_TIMESTAMP)에
    // 의존한다. insertable=false/updatable=false 이므로 JPA 가 INSERT/UPDATE 컬럼에서 제외.
    // 생성자에서 값을 받지만 실제 DB 값은 서버 시간으로 세팅되고, save 직후 엔티티의 필드는
    // null 일 수 있다. 필요시 entityManager.refresh(entity) 로 동기화. 현재 코드는 해당 값
    // 을 save 직후 읽지 않으므로 문제 없음.
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected ProductionOrder() {
    }

    public ProductionOrder(String productionOrderId,
                           String poId,
                           String poCode,
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
        this(productionOrderId, 0L, poId, orderDate, clientId, managerId, dueDate, status,
                clientName, country, managerName, itemName, linkedDocuments, createdAt, updatedAt);
    }

    public ProductionOrder(String productionOrderId,
                           Long poId,
                           String poCode,
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
        this.productionOrderCode = productionOrderId;
        this.poId = poId;
        this.poCode = poCode;
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
                           String poCode,
                           LocalDate orderDate,
                           LocalDate dueDate,
                           String status,
                           List<String> items,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this(productionOrderId, 0L, poId, orderDate, dueDate, status, items, createdAt, updatedAt);
    }

    public ProductionOrder(String productionOrderId,
                           Long poId,
                           String poCode,
                           LocalDate orderDate,
                           LocalDate dueDate,
                           String status,
                           List<String> items,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this(productionOrderId, poId, poCode, orderDate, 0, null, dueDate, status,
                null, null, null, null, null, createdAt, updatedAt);
        this.items = items;
    }

    public Long getProductionOrderPk() {
        return productionOrderPk;
    }

    public String getProductionOrderId() {
        return productionOrderCode;
    }

    public String getPoId() {
        return poCode != null ? poCode : (poId == null ? null : String.valueOf(poId));
    }

    public Long getPurchaseOrderId() {
        return poId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getPoNo() {
        return poCode;
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

    public String getItemsSnapshot() {
        return itemsSnapshot;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
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
