package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.PackingListQueryMapper;
import com.team2.documents.query.model.PackingListView;

@Service
public class PackingListQueryService {

    private final PackingListQueryMapper packingListQueryMapper;

    public PackingListQueryService(PackingListQueryMapper packingListQueryMapper) {
        this.packingListQueryMapper = packingListQueryMapper;
    }

    public PackingListView findById(String plId) {
        PackingListView packingList = packingListQueryMapper.findById(plId);
        if (packingList == null) {
            throw new ResourceNotFoundException("PL 정보를 찾을 수 없습니다.");
        }
        return packingList;
    }

    public List<PackingListView> findAll() {
        return packingListQueryMapper.findAll();
    }
}
