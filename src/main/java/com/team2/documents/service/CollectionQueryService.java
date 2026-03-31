package com.team2.documents.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Collection;
import com.team2.documents.mapper.CollectionQueryMapper;

@Service
public class CollectionQueryService {

    private final CollectionQueryMapper collectionQueryMapper;

    public CollectionQueryService(CollectionQueryMapper collectionQueryMapper) {
        this.collectionQueryMapper = collectionQueryMapper;
    }

    public List<Collection> findAll() {
        return collectionQueryMapper.findAll();
    }

    public Collection findById(Long id) {
        Collection collection = collectionQueryMapper.findById(id);
        if (collection == null) {
            throw new IllegalArgumentException("매출·수금 현황 정보를 찾을 수 없습니다.");
        }
        return collection;
    }
}
