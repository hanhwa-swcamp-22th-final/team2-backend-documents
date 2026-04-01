package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.command.domain.entity.PackingList;

@Mapper
public interface PackingListQueryMapper {
    PackingList findById(String plId);
    List<PackingList> findAll();
}
