package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.ProformaInvoiceView;

@Mapper
public interface ProformaInvoiceQueryMapper {

    ProformaInvoiceView findById(String piId);

    List<ProformaInvoiceView> findAll();

    List<ProformaInvoiceView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * managerIdScope 지정 시 해당 user_id 목록 소속 PI 만 반환. null 이면 전체 (ADMIN).
     * 빈 리스트가 오는 케이스는 서비스 레이어에서 조기 반환 처리 (IN () 금지).
     */
    List<ProformaInvoiceView> findPageScoped(@Param("offset") int offset,
                                              @Param("limit") int limit,
                                              @Param("managerIds") List<Long> managerIds);

    long countAll();

    long countScoped(@Param("managerIds") List<Long> managerIds);
}
