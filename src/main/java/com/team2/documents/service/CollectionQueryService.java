package com.team2.documents.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Collection;
import com.team2.documents.repository.CollectionRepository;

@Service
public class CollectionQueryService {

    private final CollectionRepository collectionRepository;

    public CollectionQueryService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<Collection> findAll() {
        return collectionRepository.findAll();
    }

    public Collection findById(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("매출·수금 현황 정보를 찾을 수 없습니다."));
    }
}
