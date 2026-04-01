package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.command.domain.entity.Collection;

@Mapper
public interface CollectionQueryMapper {

    Collection findById(Long id);

    List<Collection> findAll();
}
