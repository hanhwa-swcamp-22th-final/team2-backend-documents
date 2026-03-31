package com.team2.documents.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.team2.documents.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
