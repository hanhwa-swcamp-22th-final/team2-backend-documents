package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.ApprovalRequestView;

@Mapper
public interface ApprovalRequestQueryMapper {

    ApprovalRequestView findById(Long approvalRequestId);

    ApprovalRequestView findByDocumentTypeAndDocumentIdAndStatus(
            @Param("documentType") String documentType,
            @Param("documentId") String documentId,
            @Param("status") String status);

    List<ApprovalRequestView> findAll();

    List<ApprovalRequestView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
