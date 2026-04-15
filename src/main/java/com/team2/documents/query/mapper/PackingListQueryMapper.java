package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.team2.documents.query.model.PackingListView;

@Mapper
public interface PackingListQueryMapper {
    PackingListView findById(String plId);
    List<PackingListView> findAll();

    List<PackingListView> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long countAll();
}
