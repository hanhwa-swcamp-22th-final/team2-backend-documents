package com.team2.documents.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team2.documents.command.domain.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    long countByPoId(Long poId);

    long countByPoIdAndStatus(Long poId, String status);
}
