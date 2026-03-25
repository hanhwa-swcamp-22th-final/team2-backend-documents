-- ============================================================
-- Activity Service DDL
-- 서비스 설명: 영업 활동 및 거래 관리 관련 테이블
--   - activities: 영업 활동 기록 (미팅, 이슈, 메모, 일정)
--   - contacts: 거래처 담당자 연락처
--   - email_logs: 메일 발송 이력
--   - email_log_types: 메일 첨부 문서 유형
--   - email_log_attachments: 메일 첨부파일
-- Engine: InnoDB | Charset: utf8mb4_unicode_ci
-- ============================================================

-- 의존성 역순으로 DROP
DROP TABLE IF EXISTS email_log_attachments;
DROP TABLE IF EXISTS email_log_types;
DROP TABLE IF EXISTS email_logs;
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS activities;

-- ------------------------------------------------------------
-- 1. activities (영업 활동 기록)
-- ------------------------------------------------------------
CREATE TABLE activities (
    activity_id         INT                                          NOT NULL AUTO_INCREMENT,
    client_id           INT                                          NOT NULL COMMENT 'FK→master.clients',
    po_id               VARCHAR(30)                                  NULL     COMMENT 'FK→document.purchase_orders',
    activity_author_id  INT                                          NOT NULL COMMENT 'FK→auth.users',
    activity_date       DATE                                         NOT NULL,
    activity_type       ENUM('미팅/협의','이슈','메모/노트','일정')    NOT NULL,
    activity_title      VARCHAR(100)                                 NOT NULL,
    activity_content       TEXT                                         NULL,
    activity_priority      ENUM('높음','보통')                            NULL     COMMENT '이슈 타입 우선순위',
    activity_schedule_from DATE                                         NULL     COMMENT '일정 타입 시작일',
    activity_schedule_to   DATE                                         NULL     COMMENT '일정 타입 종료일',
    created_at             TIMESTAMP                                    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (activity_id),
    INDEX idx_activities_client_id (client_id),
    INDEX idx_activities_po_id (po_id),
    INDEX idx_activities_activity_author_id (activity_author_id),
    INDEX idx_activities_activity_date (activity_date),
    INDEX idx_activities_activity_type (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. contacts (거래처 관련 연락처)
-- ------------------------------------------------------------
CREATE TABLE contacts (
    contact_id       INT          NOT NULL AUTO_INCREMENT,
    client_id        INT          NOT NULL COMMENT 'FK→master.clients',
    contact_name     VARCHAR(100) NOT NULL,
    contact_position VARCHAR(100) NULL,
    contact_email    VARCHAR(255) NULL,
    contact_tel      VARCHAR(50)  NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (contact_id),
    INDEX idx_contacts_client_id (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. email_logs (메일 이력)
-- ------------------------------------------------------------
CREATE TABLE email_logs (
    email_log_id         INT                        NOT NULL AUTO_INCREMENT,
    client_id            INT                        NOT NULL COMMENT 'FK→master.clients',
    po_id                VARCHAR(30)                NULL     COMMENT 'FK→document.purchase_orders',
    email_title          VARCHAR(200)               NOT NULL,
    email_recipient_name VARCHAR(100)               NULL,
    email_recipient_email VARCHAR(255)              NOT NULL,
    email_sender_id      INT                        NOT NULL COMMENT 'FK→auth.users',
    email_status         ENUM('발송','실패')         NOT NULL DEFAULT '발송',
    email_sent_at        TIMESTAMP                  NULL,
    created_at           TIMESTAMP                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (email_log_id),
    INDEX idx_email_logs_client_id (client_id),
    INDEX idx_email_logs_po_id (po_id),
    INDEX idx_email_logs_email_sender_id (email_sender_id),
    INDEX idx_email_logs_email_status (email_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. email_log_types (메일 첨부 문서 유형)
-- ------------------------------------------------------------
CREATE TABLE email_log_types (
    email_log_type_id INT                              NOT NULL AUTO_INCREMENT,
    email_log_id      INT                              NOT NULL,
    email_doc_type    ENUM('PI','CI','PL','생산지시서','출하지시서')   NOT NULL,
    PRIMARY KEY (email_log_type_id),
    INDEX idx_email_log_types_email_log_id (email_log_id),
    CONSTRAINT fk_email_log_types_email_log FOREIGN KEY (email_log_id) REFERENCES email_logs (email_log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. email_log_attachments (메일 첨부파일)
-- ------------------------------------------------------------
CREATE TABLE email_log_attachments (
    email_log_attachment_id INT          NOT NULL AUTO_INCREMENT,
    email_log_id            INT          NOT NULL,
    email_attachment_filename VARCHAR(255) NOT NULL,
    PRIMARY KEY (email_log_attachment_id),
    INDEX idx_email_log_attachments_email_log_id (email_log_id),
    CONSTRAINT fk_email_log_attachments_email_log FOREIGN KEY (email_log_id) REFERENCES email_logs (email_log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
