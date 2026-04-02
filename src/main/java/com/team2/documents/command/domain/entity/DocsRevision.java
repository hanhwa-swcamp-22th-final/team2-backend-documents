package com.team2.documents.command.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "docs_revision")
public class DocsRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "docs_revision_id")
    private Long docsRevisionId;

    @Column(name = "doc_type", nullable = false, length = 50)
    private String docType;

    @Column(name = "doc_id", nullable = false)
    private Long docId;

    @Column(name = "snapshot_data", nullable = false, columnDefinition = "TEXT")
    private String snapshotData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected DocsRevision() {
    }

    public DocsRevision(String docType, Long docId, String snapshotData) {
        this.docType = docType;
        this.docId = docId;
        this.snapshotData = snapshotData;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getDocsRevisionId() {
        return docsRevisionId;
    }

    public String getDocType() {
        return docType;
    }

    public Long getDocId() {
        return docId;
    }

    public String getSnapshotData() {
        return snapshotData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
