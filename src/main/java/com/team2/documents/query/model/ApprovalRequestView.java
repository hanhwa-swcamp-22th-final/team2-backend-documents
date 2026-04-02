package com.team2.documents.query.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovalRequestView {
    private Long approvalRequestId;
    private String documentType;
    private String documentId;
    private String requestType;
    private Long requesterId;
    private Long approverId;
    private String comment;
    private String reviewSnapshot;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private String status;
}
