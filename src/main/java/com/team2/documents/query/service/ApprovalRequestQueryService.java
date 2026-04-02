package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ApprovalRequestQueryMapper;
import com.team2.documents.query.model.ApprovalRequestView;

@Service
public class ApprovalRequestQueryService {

    private final ApprovalRequestQueryMapper approvalRequestQueryMapper;

    public ApprovalRequestQueryService(ApprovalRequestQueryMapper approvalRequestQueryMapper) {
        this.approvalRequestQueryMapper = approvalRequestQueryMapper;
    }

    public ApprovalRequestView findById(Long approvalRequestId) {
        ApprovalRequestView approvalRequest = approvalRequestQueryMapper.findById(approvalRequestId);
        if (approvalRequest == null) {
            throw new ResourceNotFoundException("결재 요청 정보를 찾을 수 없습니다.");
        }
        return approvalRequest;
    }

    public ApprovalRequestView findByDocumentTypeAndDocumentIdAndStatus(String documentType, String documentId, String status) {
        ApprovalRequestView approvalRequest = approvalRequestQueryMapper.findByDocumentTypeAndDocumentIdAndStatus(documentType, documentId, status);
        if (approvalRequest == null) {
            throw new ResourceNotFoundException("결재 요청 정보를 찾을 수 없습니다.");
        }
        return approvalRequest;
    }

    public List<ApprovalRequestView> findAll() {
        return approvalRequestQueryMapper.findAll();
    }
}
