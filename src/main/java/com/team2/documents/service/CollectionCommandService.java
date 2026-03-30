package com.team2.documents.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Collection;
import com.team2.documents.repository.CollectionRepository;

@Service
public class CollectionCommandService {

    private final CollectionRepository collectionRepository;

    public CollectionCommandService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public Collection complete(Long id, String status, LocalDate collectionCompletedDate) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("매출·수금 현황 정보를 찾을 수 없습니다."));
        if (!"수금완료".equals(status)) {
            throw new IllegalArgumentException("수금완료 상태만 처리할 수 있습니다.");
        }
        if (!"미수금".equals(collection.getStatus())) {
            throw new IllegalStateException("미수금 상태의 현황만 수금완료 처리할 수 있습니다.");
        }
        collection.setStatus("수금완료");
        collection.setCollectionDate(collectionCompletedDate);
        return collection;
    }
}
