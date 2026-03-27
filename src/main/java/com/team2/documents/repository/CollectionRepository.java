package com.team2.documents.repository;

import java.util.List;
import java.util.Optional;

import com.team2.documents.entity.Collection;

public interface CollectionRepository {

    List<Collection> findAll();

    Optional<Collection> findById(Long id);
}
