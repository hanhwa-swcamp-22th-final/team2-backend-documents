package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.dto.PagedResult;
import com.team2.documents.query.mapper.CollectionQueryMapper;
import com.team2.documents.query.model.CollectionView;

@Service
public class CollectionQueryService {

    private final CollectionQueryMapper collectionQueryMapper;

    public CollectionQueryService(CollectionQueryMapper collectionQueryMapper) {
        this.collectionQueryMapper = collectionQueryMapper;
    }

    public CollectionView findById(Long id) {
        CollectionView collection = collectionQueryMapper.findById(id);
        if (collection == null) {
            throw new ResourceNotFoundException("매출·수금 현황 정보를 찾을 수 없습니다.");
        }
        return collection;
    }

    public List<CollectionView> findAll() {
        return collectionQueryMapper.findAll();
    }

    public PagedResult<CollectionView> findAll(int page, int size) {
        int offset = page * size;
        List<CollectionView> content = collectionQueryMapper.findPage(offset, size);
        long total = collectionQueryMapper.countAll();
        return new PagedResult<>(content, total);
    }
}
