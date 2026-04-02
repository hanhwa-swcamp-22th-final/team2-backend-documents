package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DocsRevisionQueryMapper {
    List<String> findRevisionPayloadsByDocTypeAndDocId(@Param("docType") String docType, @Param("docId") Long docId);
}
