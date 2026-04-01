package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.query.mapper.PackingListQueryMapper;

@Service
public class PackingListQueryService {

    private final PackingListQueryMapper packingListQueryMapper;

    public PackingListQueryService(PackingListQueryMapper packingListQueryMapper) {
        this.packingListQueryMapper = packingListQueryMapper;
    }

    public PackingList findById(String plId) {
        PackingList packingList = packingListQueryMapper.findById(plId);
        if (packingList == null) {
            throw new IllegalArgumentException("PL 정보를 찾을 수 없습니다.");
        }
        return packingList;
    }

    public List<PackingList> findAll() {
        return packingListQueryMapper.findAll();
    }
}
