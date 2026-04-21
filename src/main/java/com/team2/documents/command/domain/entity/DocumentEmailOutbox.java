package com.team2.documents.command.domain.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.team2.documents.command.domain.entity.enums.DocumentEmailOutboxStatus;
import com.team2.documents.command.domain.entity.enums.DocumentEmailOutboxType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "document_email_outbox",
        indexes = {
                @Index(name = "idx_document_email_outbox_ready", columnList = "status,next_attempt_at,created_at"),
                @Index(name = "idx_document_email_outbox_type", columnList = "event_type,document_code")
        }
)
public class DocumentEmailOutbox {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final int ERROR_MESSAGE_MAX_LENGTH = 2000;
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private DocumentEmailOutboxType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DocumentEmailOutboxStatus status = DocumentEmailOutboxStatus.PENDING;

    @Column(name = "document_code", nullable = false, length = 50)
    private String documentCode;

    @Column(name = "po_code", length = 50)
    private String poCode;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected DocumentEmailOutbox() {
    }

    private DocumentEmailOutbox(DocumentEmailOutboxType eventType, String documentCode, String poCode) {
        this.eventType = eventType;
        this.documentCode = documentCode;
        this.poCode = poCode;
    }

    public static DocumentEmailOutbox approvedPiToBuyer(String piCode) {
        return new DocumentEmailOutbox(DocumentEmailOutboxType.APPROVED_PI_TO_BUYER, piCode, null);
    }

    public static DocumentEmailOutbox shipmentOrderToShippingTeam(String shipmentOrderCode, String poCode) {
        return new DocumentEmailOutbox(DocumentEmailOutboxType.SHIPMENT_ORDER_TO_SHIPPING_TEAM,
                shipmentOrderCode,
                poCode);
    }

    public static DocumentEmailOutbox productionOrderToProductionTeam(String productionOrderCode, String poCode) {
        return new DocumentEmailOutbox(DocumentEmailOutboxType.PRODUCTION_ORDER_TO_PRODUCTION_TEAM,
                productionOrderCode,
                poCode);
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (nextAttemptAt == null) {
            nextAttemptAt = now;
        }
        if (status == null) {
            status = DocumentEmailOutboxStatus.PENDING;
        }
        if (maxAttempts <= 0) {
            maxAttempts = DEFAULT_MAX_ATTEMPTS;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = now();
    }

    public boolean isReadyToProcess(LocalDateTime now) {
        return DocumentEmailOutboxStatus.PENDING.equals(status)
                && attempts < maxAttempts
                && (nextAttemptAt == null || !nextAttemptAt.isAfter(now));
    }

    public void markProcessing() {
        this.status = DocumentEmailOutboxStatus.PROCESSING;
        this.errorMessage = null;
    }

    public void markSent() {
        this.status = DocumentEmailOutboxStatus.SENT;
        this.processedAt = now();
        this.errorMessage = null;
    }

    public void markFailed(String message) {
        this.attempts += 1;
        this.errorMessage = truncate(message);
        if (attempts >= maxAttempts) {
            this.status = DocumentEmailOutboxStatus.FAILED;
            this.processedAt = now();
            return;
        }
        this.status = DocumentEmailOutboxStatus.PENDING;
        this.nextAttemptAt = now().plusMinutes(Math.min(15, attempts * 2L));
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(KOREA_ZONE);
    }

    private String truncate(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        return message.length() <= ERROR_MESSAGE_MAX_LENGTH
                ? message
                : message.substring(0, ERROR_MESSAGE_MAX_LENGTH);
    }

    public Long getId() {
        return id;
    }

    public DocumentEmailOutboxType getEventType() {
        return eventType;
    }

    public DocumentEmailOutboxStatus getStatus() {
        return status;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public String getPoCode() {
        return poCode;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public LocalDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }
}
