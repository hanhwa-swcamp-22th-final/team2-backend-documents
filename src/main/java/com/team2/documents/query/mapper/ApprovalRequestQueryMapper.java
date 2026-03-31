package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.entity.ApprovalRequest;

@Mapper
public interface ApprovalRequestQueryMapper {

    ApprovalRequest findById(Long approvalRequestId);

    ApprovalRequest findByDocumentTypeAndDocumentIdAndStatus(
            @Param("documentType") String documentType,
            @Param("documentId") String documentId,
            @Param("status") String status);

    List<ApprovalRequest> findAll();
}
