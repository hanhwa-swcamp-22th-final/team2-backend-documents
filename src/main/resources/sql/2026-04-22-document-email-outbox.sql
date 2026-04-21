CREATE TABLE IF NOT EXISTS document_email_outbox (
    outbox_id BIGINT NOT NULL AUTO_INCREMENT,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    document_code VARCHAR(50) NOT NULL,
    po_code VARCHAR(50) NULL,
    attempts INT NOT NULL DEFAULT 0,
    max_attempts INT NOT NULL DEFAULT 3,
    next_attempt_at DATETIME(6) NOT NULL,
    processed_at DATETIME(6) NULL,
    error_message TEXT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (outbox_id),
    INDEX idx_document_email_outbox_ready (status, next_attempt_at, created_at),
    INDEX idx_document_email_outbox_type (event_type, document_code)
);
