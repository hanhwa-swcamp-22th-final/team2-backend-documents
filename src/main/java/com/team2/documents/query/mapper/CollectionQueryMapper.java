package com.team2.documents.query.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.team2.documents.query.model.CollectionView;

@Mapper
public interface CollectionQueryMapper {

    CollectionView findById(Long id);

    List<CollectionView> findAll();
}
