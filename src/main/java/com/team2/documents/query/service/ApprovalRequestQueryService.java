package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.query.mapper.ApprovalRequestQueryMapper;

@Service
public class ApprovalRequestQueryService {

    private final ApprovalRequestQueryMapper approvalRequestQueryMapper;

    public ApprovalRequestQueryService(ApprovalRequestQueryMapper approvalRequestQueryMapper) {
        this.approvalRequestQueryMapper = approvalRequestQueryMapper;
    }

    public ApprovalRequest findById(Long approvalRequestId) {
        ApprovalRequest approvalRequest = approvalRequestQueryMapper.findById(approvalRequestId);
        if (approvalRequest == null) {
            throw new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다.");
        }
        return approvalRequest;
    }

    public ApprovalRequest findByDocumentTypeAndDocumentIdAndStatus(String documentType, String documentId, String status) {
        ApprovalRequest approvalRequest = approvalRequestQueryMapper.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status);
        if (approvalRequest == null) {
            throw new IllegalArgumentException("결재 요청 정보를 찾을 수 없습니다.");
        }
        return approvalRequest;
    }

    public List<ApprovalRequest> findAll() {
        return approvalRequestQueryMapper.findAll();
    }
}
