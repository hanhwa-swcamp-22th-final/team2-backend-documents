package com.team2.documents.command.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_number_sequences")
public class DocumentNumberSequence {

    @Id
    @Column(name = "prefix", nullable = false, length = 20)
    private String prefix;

    @Column(name = "last_number", nullable = false)
    private Long lastNumber;

    protected DocumentNumberSequence() {
    }

    public DocumentNumberSequence(String prefix, Long lastNumber) {
        this.prefix = prefix;
        this.lastNumber = lastNumber;
    }

    public String getPrefix() {
        return prefix;
    }

    public Long getLastNumber() {
        return lastNumber;
    }

    public void increment() {
        this.lastNumber = (lastNumber == null ? 0L : lastNumber) + 1L;
    }
}
